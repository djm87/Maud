/*
 * @(#)JSubordSListPane.java created 01/01/1997 Mesiano
 *
 * Copyright (c) 1997 Luca Lutterotti All Rights Reserved.
 *
 * This software is the research result of Luca Lutterotti and it is
 * provided as it is as confidential and proprietary information.
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with the author.
 *
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package it.unitn.ing.rista.awt;

import it.unitn.ing.rista.diffr.*;
import java.awt.Frame;

/**
 * The JSubordSListPane is a class
 *
 * @version $Revision: 1.3 $, $Date: 2004/08/12 09:36:03 $
 * @author Luca Lutterotti
 * @since JDK1.1
 */

public class JSubordSListPane extends JSubordListPane {

  public JSubordSListPane(Frame parent, boolean showTotal) {
    super(parent, showTotal);
  }

  public void setparameterlist() {
    if (itsparent != null && thelist != null) {
      XRDcat obj = (XRDcat) itsparent.subordinateloopField[theindex].selectedElement();
      if (obj != null)
        for (int i = 0; i < fieldNumber; i++)
          valueTF[i].setText(obj.getString(i));
    }
  }

  public void retrieveparlist(int numb) {
    if (numb >= 0 && itsparent != null && thelist != null) {
      XRDcat obj = (XRDcat) itsparent.subordinateloopField[theindex].elementAt(numb);
      if (obj != null)
        for (int i = 0; i < fieldNumber; i++)
          obj.setString(i, valueTF[i].getText());
    }
  }

  public void retrieveparlist() {
    if (selected >= 0 && itsparent != null && thelist != null) {
      XRDcat obj = (XRDcat) itsparent.subordinateloopField[theindex].elementAt(selected);
      if (obj != null)
        for (int i = 0; i < fieldNumber; i++)
          obj.setString(i, valueTF[i].getText());
    }
  }

}
