/*
 * @(#)XRFInstrument.java created Apr 8, 2007 Casalino
 *
 * Copyright (c) 2007 Luca Lutterotti All Rights Reserved.
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

package it.unitn.ing.rista.diffr.instrument;

import it.unitn.ing.rista.awt.*;
import it.unitn.ing.rista.diffr.XRDcat;
import it.unitn.ing.rista.diffr.Instrument;
import it.unitn.ing.rista.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The XRFInstrument is a class defining a XRF instrument
 *
 * @author Luca Lutterotti
 * @version $Revision: 1.00 $, $Date: Apr 8, 2007 9:46:18 PM $
 * @since JDK1.1
 */
public class XRFInstrument extends Instrument {
  public static String modelID = "XRF Instrument";

  public XRFInstrument(XRDcat obj, String alabel) {
    super(obj, alabel);
    identifier = modelID;
    IDlabel = modelID;
    description = modelID;
  }

  public XRFInstrument(XRDcat afile) {
    this(afile, modelID);
  }

  public XRFInstrument() {
    identifier = modelID;
    IDlabel = modelID;
    description = modelID;
  }

	public void edit(Frame aframe) {
		(new XRFInstrumentD(aframe, this)).setVisible(true);
	}

	class XRFInstrumentD extends myJFrame {

		public XRFInstrument theinstrument;

		JTextField InstrumentnameTF;
		JTextField intensityTF;
		JTextField intensityScaleTF;
		JComboBox[] optchoice;
		boolean[] active = {false, true, true, false, true, true, true};

//    JParameterListPane ThetaPanel;

		public XRFInstrumentD(Frame parent, XRFInstrument ainstrument) {

			super(parent);

			initializeSizeAndPosition(
					false, "instrumentEDXRFFrame.frameWidth", "instrumentEDXRFFrame.frameHeight", 400, 500,
					true, "instrumentEDXRFFrame.framePositionX", "instrumentEDXRFFrame.framePositionY", 50, 50);

			parent.setCursor(new Cursor(Cursor.WAIT_CURSOR));

			theinstrument = ainstrument;

			Container c1 = getContentPane();
			c1.setLayout(new BorderLayout(6, 6));

//      JTabbedPane p1 = new JTabbedPane();
//      c1.add(p1, BorderLayout.CENTER);
//      String p1String[] = {"General",
//          "Errors"};

			JPanel jp1 = new JPanel();
			jp1.setLayout(new BorderLayout(6, 6));
//      p1.addTab(p1String[0], null, jp1);
			c1.add(jp1, BorderLayout.CENTER);

			JPanel jPanel12 = new JPanel();
			jPanel12.setLayout(new BorderLayout(6, 6));
			jp1.add("North", jPanel12);

			JPanel jPanel6 = new JPanel();
			jPanel6.setLayout(new GridLayout(0, 1, 3, 3));
			jPanel12.add("West", jPanel6);

			String[] tmpStringS = {"Instrument name:", "Incident intensity:", "Scale factor normalization:"};
			for (int i = 0; i < tmpStringS.length; i++)
				jPanel6.add(new JLabel(tmpStringS[i]));

			JPanel jPanel8 = new JPanel();
			jPanel8.setLayout(new GridLayout(0, 1, 3, 3));
			jPanel12.add("Center", jPanel8);

			InstrumentnameTF = new JTextField(24);
			jPanel8.add(InstrumentnameTF);
			intensityTF = new JTextField(Constants.FLOAT_FIELD);
			jPanel8.add(intensityTF);
			intensityTF.setText("1");
			intensityScaleTF = new JTextField(Constants.FLOAT_FIELD);
			jPanel8.add(intensityScaleTF);
			intensityScaleTF.setText("1");

			jPanel12 = new JPanel();
			jPanel12.setLayout(new BorderLayout(2, 2));
			jp1.add("Center", jPanel12);

			jPanel6 = new JPanel();
			jPanel6.setLayout(new GridLayout(0, 1, 1, 1));
			jPanel12.add("West", jPanel6);

			String[] tmpStringS1 = {"      Intensity calibration:",
					"      Channel calibration:",
					"      Geometry:",
					"      Measurement:",
					"      Source:",
					"      Detector:",
					"      Instrument Broadening:"};

			for (int i = 0; i < theinstrument.Nsubordinate - 1; i++)
				if (active[i])
					jPanel6.add(new JLabel(tmpStringS1[i]));

			jPanel8 = new JPanel();
			jPanel8.setLayout(new GridLayout(0, 1, 1, 1));
			jPanel12.add("Center", jPanel8);

			optchoice = new JComboBox[theinstrument.Nsubordinate - 1];
			for (int i = 0; i < theinstrument.Nsubordinate - 1; i++) { //absorption removed here
				if (active[i]) {
					final int index = i;
					JPanel jPanel2 = new JPanel();
					jPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT, 1, 1));
					jPanel8.add(jPanel2);
					optchoice[i] = new JComboBox();
					optchoice[i].setEditable(false);
					optchoice[i].setMaximumRowCount(4);
					jPanel2.add(optchoice[i]);
					JButton optbutton = new JIconButton("Eyeball.gif", "Options");
					optbutton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							subordinateOptions(index);
						}
					});
					jPanel2.add(optbutton);
				}
			}

/*      JPanel panel1 = new JPanel(new FlowLayout());
      panel1.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED), "2theta or d-space displacement"));
      p1.addTab(p1String[1], null, panel1);

      ThetaPanel = new JParameterListPane(this, false, true);
      panel1.add(ThetaPanel);*/

			JPanel closebuttonPanel = new JPanel();
			closebuttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 3));
			c1.add("South", closebuttonPanel);
			JButton jbok1 = new JCloseButton();
			closebuttonPanel.add(jbok1);
			jbok1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					retrieveParameters();
					setVisible(false);
					dispose();
				}
			});
			getRootPane().setDefaultButton(jbok1);

			setTitle(ainstrument.toXRDcatString());
			initparameters();
			parent.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			createDefaultMenuBar();

			pack();
		}

		public Instrument getData() {
			return theinstrument;
		}

		void initparameters() {
			int i, j;

			InstrumentnameTF.setText(theinstrument.getInstrumentID());
			intensityTF.setText(theinstrument.getIntensity().getValue());
			intensityScaleTF.setText(theinstrument.getString(1));
			addComponenttolist(intensityTF, theinstrument.getIntensity());

//      ThetaPanel.setList(theinstrument, 0);

			for (i = 0; i < theinstrument.Nsubordinate - 1; i++) { //absorption removed here
				if (active[i]) {
					for (j = 0; j < theinstrument.getsubordClassNumber(i); j++)
						optchoice[i].addItem(theinstrument.getsubordIdentifier(i, j));
					optchoice[i].setSelectedItem(theinstrument.subordinateField[i].identifier);
				}
			}

		}

		public void retrieveParameters() {
			super.retrieveParameters();

			theinstrument.setInstrumentID(InstrumentnameTF.getText());
			theinstrument.getIntensity().setValue(intensityTF.getText());
			theinstrument.setString(1, intensityScaleTF.getText());

//      ThetaPanel.retrieveparlist();

			for (int i = 0; i < theinstrument.Nsubordinate - 1; i++) { //absorption removed here
				if (active[i]) {
					String value = optchoice[i].getSelectedItem().toString();
					if (theinstrument.subordinateField[i] == null ||
							!value.equals(theinstrument.subordinateField[i].identifier))
						theinstrument.setsubordinateField(i, value);
				}
			}
		}

		public void subordinateOptions(int index) {
			String value = optchoice[index].getSelectedItem().toString();
			if (theinstrument.subordinateField[index] == null ||
					!value.equals(theinstrument.subordinateField[index].identifier))
				theinstrument.setsubordinateField(index, value);

			theinstrument.subordinateField[index].getOptionsDialog(this).setVisible(true);
		}

		public void dispose() {
			for (int i = 0; i < theinstrument.Nsubordinate - 1; i++) {  //absorption removed here
				if (active[i])
					optchoice[i].removeAllItems();
			}
			optchoice = null;
			theinstrument = null;
//      ThetaPanel.dispose();

			super.dispose();
		}

	}
}
