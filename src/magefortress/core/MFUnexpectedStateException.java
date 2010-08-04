/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package magefortress.core;

public class MFUnexpectedStateException extends RuntimeException
{

  public MFUnexpectedStateException(String _msg)
  {
    super(_msg);
  }

  public MFUnexpectedStateException(String _msg, Throwable _t)
  {
    super(_msg, _t);
  }
}
