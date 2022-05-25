/*
 * @(#)GeometryXRFInstrument.java created 20/08/2013 Mesiano.
 *
 * Copyright (c) 2013 Luca Lutterotti All Rights Reserved.
 *
 * This software is the research result of Luca Lutterotti and it is
 * provided as it is as confidential and proprietary information.
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with Luca Lutterotti.
 *
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package it.unitn.ing.rista.diffr.geometry;

import java.lang.*;

import it.unitn.ing.rista.diffr.*;
import it.unitn.ing.rista.util.*;

/**
 *  The GeometryXRFInstrument is a class
 *
 *
 * @version $Revision: 1.7 $, $Date: 2013/08/20 17:14:07 $
 * @author Luca Lutterotti
 * @since JDK1.1
 */

public class GeometryXRFInstrument extends GeometryBraggBrentano {

	public GeometryXRFInstrument(XRDcat aobj, String alabel) {
		super(aobj, alabel);
		identifier = "XRF instrument";
		IDlabel = "XRF instrument";
		description = "XRF instrument geometry";
	}

	public GeometryXRFInstrument(XRDcat aobj) {
		this(aobj, "XRF instrument");
	}

	public GeometryXRFInstrument() {
		identifier = "XRF instrument";
		IDlabel = "XRF instrument";
		description = "XRF instrument geometry";
	}

	public double LorentzPolarization(DiffrDataFile adatafile, Sample asample, double position, boolean dspacingbase, boolean energyDispersive) {

		// first correction for absorption on asymmetric diffraction
		double lp = 1.0;

/*    double sintheta, sin2theta, cos2theta, Ph, width, beamsize;

    sintheta = Math.sin(position * Constants.DEGTOPI / 2.0);

    double sradius = asample.getRadiusDimensionD();
    if (sradius > 0.0)
      sradius = 2 * sradius * sintheta;
    else
      sradius = 10.0e6;
    width = asample.getEquatorialDimensionD();
    if (width <= 0.0)
      width = 10.0e6;

    width = Math.min(width, sradius);

    if (width > 0.0 && (slitaperture > 0.0 && radius > 0.0)) {
      beamsize = radius * MoreMath.sind(slitaperture) / sintheta;
      if (beamsize > width)
        lp *= width / beamsize;
    }

    double[] tilt_angles = adatafile.getTiltingAngle();

    sintheta = Math.sin((90.0 - tilt_angles[1]) * Constants.DEGTOPI);

    double height = asample.getAxialDimensionD();

    if (height > 0.0 && (slitaperture > 0.0 && radius > 0.0)) {
      beamsize = radius * MoreMath.sind(slitaperture) / sintheta;
      if (beamsize > height)
        lp *= height / beamsize;
    }*/


		return lp;
	}

	public double getBeamRelatedCorrection(DiffrDataFile adatafile, Sample asample, double position, int pointIndex) {
		// first correction for absorption on asymmetric diffraction
		double lp = 1.0, beamsize = 0;

/*		double[] incidentDiffracted = adatafile.getIncidentAndDiffractionAngles(0);
//		System.out.println(incidentDiffracted[0]);
//	  incidentDiffracted[0] *= Constants.DEGTOPI;
		double sintheta = Math.sin(incidentDiffracted[0]);

		double width = asample.getEquatorialDimensionD();

		lp = 1.0 / sintheta;
		if (slitaperture > 0.0 && radius > 0.0) {
			beamsize = 2.0 * radius * MoreMath.sind(slitaperture) / sintheta;
			lp = beamsize;
		}

		if (width > 0.0 && beamsize > width)
				lp = width;*/

/*    double[] tilt_angles = adatafile.getTiltingAngle();

    sintheta = Math.sin((90.0 - tilt_angles[1]) * Constants.DEGTOPI);

    double height = asample.getAxialDimensionD();

    if (height > 0.0 && (slitaperture > 0.0 && radius > 0.0)) {
      beamsize = radius * MoreMath.sind(slitaperture) / sintheta;
      if (beamsize > height)
        lp *= height / beamsize;
    }*/

		return lp;
	}

	public double getBeamOutCorrection(DiffrDataFile adatafile, Sample asample) {
		// first correction for absorption on asymmetric diffraction

		double lp = 1.0, beamsize = 0;

		double[] incidentDiffracted = adatafile.getIncidentAndDiffractionAngles(0);
//		System.out.println(incidentDiffracted[0]);
//	  incidentDiffracted[0] *= Constants.DEGTOPI;
		double slitApertureRad = slitaperture * Constants.DEGTOPI / 2;
		double sintheta = Math.sin(incidentDiffracted[0]); // - slitApertureRad);

		double width = asample.getEquatorialDimensionD();

		lp = 1.0 / sintheta;
		if (slitaperture > 0.0 && radius > 0.0) {
			beamsize = 2.0 * radius * Math.sin(slitApertureRad);
			lp *= beamsize;
		}

		if (width > 0.0 && beamsize > width)
			lp = width;

		return lp * 0.1;
	}
}
