/*
 *  Copyright (c) 2009 Simon Hardijanto
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */
package magefortress.map;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import magefortress.core.MFEDirection;
import magefortress.core.MFLocation;
import magefortress.creatures.behavior.movable.MFCapability;
import magefortress.creatures.behavior.movable.MFEMovementType;

/**
 * Stores all found entrances and sections.
 */
public class MFNavigationMap
{

  /**
   * Constructor
   * @param _map the underlying real map
   */
  public MFNavigationMap(MFMap _map, MFClearanceCalculator _clearanceCalculator)
  {
    this.map = _map;
    this.clearanceCalculator = _clearanceCalculator;
    this.entrances = new HashMap<MFLocation, MFSectionEntrance>();
    this.sections = new LinkedList<MFSection>();
    this.movementCombinations = new HashMap<Integer, Set<MFCapability>>();
    Set<MFCapability> setForClearance = new HashSet<MFCapability>();
    setForClearance.add(DEFAULT_CAPABILITY);
    this.movementCombinations.put(DEFAULT_CLEARANCE, setForClearance);
  }

  public MFMap getMap()
  {
    return this.map;
  }

  /**
   * Clears the list of entrances and re-calculates them for all depth levels
   * of the map.
   */
  public void calculateAllLevels()
  {
    this.entrances.clear();
    this.sections.clear();
    for (int depth = 0; depth < this.map.getDepth(); ++depth) {
      calculateLevel(depth);
    }
  }

  /**
   * Removes all entrances of the depth level and re-calculates them.
   * @param _depth the depth level
   */
  public void calculateLevel(int _depth)
  {
    // remove all entrances of the specified level before adding new ones
    for(Iterator<MFLocation> it = this.entrances.keySet().iterator(); it.hasNext(); ) {
      MFLocation entrance = it.next();
      if (entrance.z == _depth) {
        it.remove();
      }
    }
    // remove all sections of the specified level
    for(Iterator<MFSection> it = this.sections.iterator(); it.hasNext(); ) {
      MFSection section = it.next();
      if (section.getLevel() == _depth) {
        it.remove();
      }
    }
    // find and store the entrances
    final Map<MFLocation, MFSectionEntrance> levelEntrances =
                               this.findEntrances(this.map.getLevelMap(_depth));
    this.entrances.putAll(levelEntrances);
    final List<MFSection> levelSections = 
                               this.findSections(_depth, levelEntrances);
    this.sections.addAll(levelSections);
    this.findConnections(levelSections);
  }

  /**
   * Adds a combination of clearance and movement types to the list of paths,
   * that will be searched.
   */
  public void addMovementCombination(final int _clearance, final MFCapability _capability)
  {
    if (_clearance < 1) {
      String msg = "Navigation Map: Cannot add movement combination with " +
                    "clearance < 1. Got: " + _clearance;
      logger.warning(msg);
      throw new IllegalArgumentException(msg);
    }
    if (_capability == null || _capability == MFCapability.NONE) {
      String msg = "Navigation Map: Cannot add movement combination without " +
                    "any capabilities";
      logger.warning(msg);
      throw new IllegalArgumentException(msg);
    }

    Set<MFCapability> setForClearance = this.movementCombinations.get(_clearance);
    // check if a list for the clearance is already stored
    if (setForClearance == null) {
      // create a new list for the specified clearance and store it
      setForClearance = new HashSet<MFCapability>();
      this.movementCombinations.put(_clearance, setForClearance);
    }
    
    boolean isDuplicate = setForClearance.contains(_capability);
    
    // duplicate -> log warning
    if (isDuplicate) {
      StringBuilder capabilitiesText = new StringBuilder();
      for (MFEMovementType type : _capability) {
        capabilitiesText.append(type.toString());
        capabilitiesText.append(" ");
      }
      String msg = "Navigation Map: Trying to add a set of capabilities (" +
                    capabilitiesText.toString().trim() + ") already stored." ;
      logger.warning(msg);

    // add the combination to the list
    } else {
      setForClearance.add(_capability);
    }
  }

  //---vvv---  PACKAGE-PRIVATE METHODS  ---vvv---
  /**
   * Gets the list of entrances on the map. May return a zero-sized list if
   * there has not been an update triggered to calculate the entrances or there
   * are no detectable entrances on the map yet.
   * @return the unmodifiable list of entrances
   */
  List<MFSectionEntrance> getEntrances()
  {
    return new LinkedList<MFSectionEntrance>(this.entrances.values());
  }

  /**
   * Gets the list of sections on the map. May return a zero-sized list if
   * there has not been an update triggered or there are no sections on the map
   * yet.
   * @return the unmodifiable list of sections
   */
  List<MFSection> getSections()
  {
    return Collections.unmodifiableList(this.sections);
  }

  /**
   * Calculates all clearance values and stores them inside the tiles.
   * @param _capability The type of movement
   */
  public void updateClearanceValues(MFCapability _capability)
  {
    this.clearanceCalculator.calculateAllLevels(_capability);
  }

  //---vvv---      PRIVATE METHODS      ---vvv---
  private static final Logger logger = Logger.getLogger(MFNavigationMap.class.getName());
  private final static int DEFAULT_CLEARANCE = 1;
  private final static MFCapability DEFAULT_CAPABILITY = MFCapability.WALK;

  private final MFMap map;
  private final MFClearanceCalculator clearanceCalculator;
  private final Map<MFLocation, MFSectionEntrance> entrances;
  private final List<MFSection> sections;
  private final Map<Integer, Set<MFCapability>> movementCombinations;

  /**
   * Scans the map and detects all tiles which might define an entrance. For
   * navigational use.
   * @return the list of entrances found
   */
  private Map<MFLocation, MFSectionEntrance> findEntrances(final MFTile[][] _tiles)
  {
    final HashMap<MFLocation, MFSectionEntrance> potentialEntrances =
                                  new HashMap<MFLocation, MFSectionEntrance>();

    for (final MFTile[] rows : _tiles) {
      for (final MFTile tile : rows) {
        boolean isEntrance = true;
        // skip if tile is not underground or not dug out or without a floor
        if (!tile.isUnderground() || !tile.isDugOut() || !tile.hasFloor()) {
          isEntrance = false;
        }
        // skip if clearance > 1
        //if (tile.getClearance(MFEMovementType.WALK) > 1){
        //  continue;
        //}
        // skip if a neighbor is an entrance
        if (isEntrance && hasNeighboringEntrance(tile, potentialEntrances)) {
          isEntrance = false;
        }
        // skip this tile if it doesn't divide two groups
        if (isEntrance && !dividesGroups(tile)) {
          isEntrance = false;
        }

        // all tests passed - entrance found!
        if (isEntrance) {
          MFSectionEntrance entrance = new MFSectionEntrance(tile);
          potentialEntrances.put(tile.getLocation(), entrance);
        // remove entrance from tile
        } else {
          tile.setEntrance(null);
        }

      }
    }
    Map<MFLocation, MFSectionEntrance> result = this.collapseCloseEntrances(potentialEntrances);
    return result;
  }

  /**
   * Checks if a neighbor is already defined as an entrance.
   * @param _tile the tile to check
   * @param _entrances the list of entrances
   * @return <code>true</true> if a neighbor is an entrance
   */
  private boolean hasNeighboringEntrance(final MFTile _tile,
                        final HashMap<MFLocation, MFSectionEntrance> _entrances)
  {
    MFEDirection[] previous = {MFEDirection.N, MFEDirection.NW, MFEDirection.W};
    for (MFEDirection direction : previous) {
      MFTile neighbor = this.map.getNeighbor(_tile, direction);
      if (neighbor != null && _entrances.containsKey(neighbor.getLocation())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a tile divides at least two groups of tiles, one consisting of
   * at least 2 tiles.
   * @param _tile the tile to check
   * @return <code>true</true> if the tile divides multiple groups
   */
  private boolean dividesGroups(final MFTile _tile)
  {
    int biggestGroupSize = 0;
    int currentGroupSize = 0;
    int blockedStretchesCount = 0;
    boolean blockedStretch = false;
    boolean blockedN = false;
    boolean blockedNW = false;

    for (MFEDirection direction : MFEDirection.values()) {
      // get the neighboring tile
      MFTile neighbor = this.map.getNeighbor(_tile, direction);
      boolean reachable = this.map.canWalkTo(_tile, neighbor, direction);

      // unreachable or irrelevant or unwalkable tile
      if (!reachable || !neighbor.isUnderground()) {
        // switch to empty stretch
        if (blockedStretch == false) {
          if (currentGroupSize > biggestGroupSize) {
            biggestGroupSize = currentGroupSize;
          }
          currentGroupSize = 0;
          ++blockedStretchesCount;
          blockedStretch = true;
        }
      // neighbor is walkable and reachable
      } else if (neighbor.isWalkable(MFEMovementType.WALK)) {
        ++currentGroupSize;
        blockedStretch = false;
      } else {
        String msg = "Navigation Map: error calculating entrance for " + _tile + "/neighbor: " + neighbor;
        logger.warning(msg);
      }

      // save for wrap test
      if (direction == MFEDirection.N) {
        blockedN = blockedStretch;
      } else if (direction == MFEDirection.NW) {
        blockedNW = blockedStretch;
      }
    }
    // final check for biggest group
    if (currentGroupSize > biggestGroupSize) {
      biggestGroupSize = currentGroupSize;
    }
    // wrap empty stretches blocks
    if (blockedN && blockedNW) {
      --blockedStretchesCount;
    }

    if (blockedStretchesCount < 2 || biggestGroupSize < 2) {
      return false;
    }
    return true;
  }

  /**
   * Moves all entrances lying two tiles apart into one if possible.
   * @param _entrances list of entrances
   * @return collapsed list of entrances
   */
  private Map<MFLocation, MFSectionEntrance> collapseCloseEntrances(
                        final HashMap<MFLocation, MFSectionEntrance> _entrances)
  {
    Map<MFLocation, MFSectionEntrance> result = new HashMap<MFLocation, MFSectionEntrance>();
    List<MFSectionEntrance> removedEntrances = new LinkedList<MFSectionEntrance>();

    Iterator<MFSectionEntrance> it1 = _entrances.values().iterator();
    while (it1.hasNext()) {

      MFSectionEntrance start = it1.next();
      if (removedEntrances.contains(start))
        continue;

      MFSectionEntrance add = start;
      for (MFSectionEntrance goal : _entrances.values()) {

        MFLocation startLoc = start.getLocation();
        MFLocation goalLoc = goal.getLocation();
        // found potentially collapsable locations
        if (startLoc.distanceTo(goalLoc) == 2) {

          MFEDirection dir = startLoc.directionOf(goalLoc);
          MFTile middle = this.map.getNeighbor(this.map.getTile(startLoc), dir);
          // check if tile can be connected
          if (middle.isWalkable(MFEMovementType.WALK) && middle.isUnderground()) {
            add = new MFSectionEntrance(middle);
            removedEntrances.add(goal);
            goal.getTile().setEntrance(null);
            break;

          } //-- connectable
        } //-- potentially collapsable
      } //-- inner loop

      result.put(add.getLocation(), add);
      if (start != add) {
        it1.remove();
        start.getTile().setEntrance(null);
      }
    } //-- outer loop
    return result;
  }

  /**
   * Assigns all tiles of a level to exactly one newly created section.
   * @param _depth which level to scan for sections
   * @param _entrances the list of entrances on this level
   * @return a list of sections
   */
  private List<MFSection> findSections(final int _depth,
                                      final Map<MFLocation, MFSectionEntrance> _entrances)
  {
    final List<MFSection> result = new LinkedList<MFSection>();

    for (MFTile[] row : this.map.getLevelMap(_depth)) {
      for (MFTile tile : row) {
        // skip tiles that are not underground or blocked
        if (!tile.isUnderground() || !tile.isWalkable(MFEMovementType.WALK) || tile.isEntrance()) {
          continue;
        }

        // get top and left neighbors
        final MFTile neighborN = this.map.getNeighbor(tile, MFEDirection.N);
        final MFTile neighborW = this.map.getNeighbor(tile, MFEDirection.W);
        boolean hasNonEntranceNeighborN = this.map.canWalkTo(tile, neighborN, MFEDirection.N);
        boolean hasNonEntranceNeighborW = this.map.canWalkTo(tile, neighborW, MFEDirection.W);
        // get the reachable surrounding entrances
        MFSectionEntrance entranceN = null;
        MFSectionEntrance entranceW = null;
        if (hasNonEntranceNeighborN) entranceN = neighborN.getEntrance();
        if (hasNonEntranceNeighborW) entranceW = neighborW.getEntrance();
        hasNonEntranceNeighborN &= (entranceN == null);
        hasNonEntranceNeighborW &= (entranceW == null);

        MFSection section;
        // tile has no neighbors -> start a new section
        if (!hasNonEntranceNeighborN && !hasNonEntranceNeighborW) {
          section = new MFSection(this.map, _depth);
          result.add(section);
        // tile is connected to two neighbors -> check if they belong to different
        // sections
        } else if (hasNonEntranceNeighborN && hasNonEntranceNeighborW) {
          MFSection sectionN = neighborN.getParentSection();
          MFSection sectionW = neighborW.getParentSection();
          // same section -> add tile to one of it
          if (sectionN == sectionW) {
            section = sectionN;
          // different sections -> tile connects both sections -> unite them
          } else {
            section = uniteSections(sectionN, sectionW, result);
          }
        // tile is connected to one neighbor -> add tile to neighbor's section
        } else {
          MFTile neighbor = (hasNonEntranceNeighborN ? neighborN : neighborW);
          section = neighbor.getParentSection();
        }
        section.addTile(tile);

        // add entrances to the section
        MFSectionEntrance entranceS = null;
        MFSectionEntrance entranceE = null;
        final MFTile neighborS = this.map.getNeighbor(tile, MFEDirection.S);
        final MFTile neighborE = this.map.getNeighbor(tile, MFEDirection.E);
        if (this.map.canWalkTo(tile, neighborS, MFEDirection.S)) {
          entranceS = neighborS.getEntrance();
        }
        if (this.map.canWalkTo(tile, neighborE, MFEDirection.E)) {
          entranceE = neighborE.getEntrance();
        }

        final MFSection currentSection = tile.getParentSection();
        if (entranceN != null) {
          currentSection.addEntrance(entranceN);
          if (neighborN.getParentSection() == null) {
            currentSection.addTile(neighborN);
          }
        }
        if (entranceE != null) {
          currentSection.addEntrance(entranceE);
          if (neighborE.getParentSection() == null) {
            currentSection.addTile(neighborE);
          }
        }
        if (entranceS != null) {
          currentSection.addEntrance(entranceS);
          if (neighborS.getParentSection() == null) {
            currentSection.addTile(neighborS);
          }
        }
        if (entranceW != null) {
          currentSection.addEntrance(entranceW);
          if (neighborW.getParentSection() == null) {
            currentSection.addTile(neighborW);
          }
        }

      }//-- column loop
    }//-- row loop
    
    return result;
  }

  /**
   * Unites two sections, adds and removes the empty one from the list of sections.
   * @param _section1 The first section
   * @param _section2 The second section
   * @param _sections The list of sections
   */
  private MFSection uniteSections(final MFSection _section1, final MFSection _section2,
                                  final List<MFSection> _sections)
  {
    MFSection result = _section1.uniteWith(_section2);
    // remove empty section
    if (_section1.getSize() == 0) {
      _sections.remove(_section1);
    } else {
      _sections.remove(_section2);
    }

    return result;
  }

  /**
   * Adds connecting edges to all entrances in the same section for all possible
   * combinations specified by earlier configuration.
   * @param _sections the sections which will be searched for connections
   */
  private void findConnections(final List<MFSection> _sections)
  {
    for (int clearance : this.movementCombinations.keySet()) {
      for (MFCapability capability : this.movementCombinations.get(clearance)) {
        this.connectEntrances(_sections, clearance, capability);
      }
    }
  }

  /**
   * Adds connecting edges to all entrances in the same section for a given
   * clearance and a set of capabilities.
   * @param _sections the sections which will be used
   * @param _clearance the clearance being used to test if a tile is accessible
   * @param _capability the movement types a creature can use to access a tile
   */
  private void connectEntrances(final List<MFSection> _sections, 
          final int _clearance, MFCapability _capability)
  {
    for (MFSection section : _sections) {
      for (MFSectionEntrance startEntrance : section.getEntrances()) {
        for (MFSectionEntrance goalEntrance : section.getEntrances()) {

          // skip id
          if (startEntrance == goalEntrance) {
            continue;
          }

          // initialize search algorithm
          final MFAnnotatedAStar search = new MFAnnotatedAStar(this.map, startEntrance.getTile(),
                  goalEntrance.getTile(), _clearance, _capability);

          // search!
          MFAnnotatedPath foundPath = (MFAnnotatedPath) search.findPath();

          if (foundPath == null) {
            String msg = "Navigation Map: Unable to connect entrances to the same section. " +
                          "From " + startEntrance.getLocation() +
                          " to "  + goalEntrance.getLocation();
            logger.severe(msg);
            throw new RuntimeException(msg);
          }

          // create the edge
          MFEdge edge = new MFEdge(startEntrance, goalEntrance,
                              foundPath.getCost(), _clearance, _capability);
          startEntrance.addEdge(edge);
        }
      }
    }
  }
}
