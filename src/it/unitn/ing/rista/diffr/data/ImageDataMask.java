
/*
 * @(#)ImageDataMask.java created 5/11/2021 DII, Povo
 *
 * Copyright (c) 2021 Luca Lutterotti All Rights Reserved.
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

package it.unitn.ing.rista.diffr.data;

import ij.ImagePlus;
import it.unitn.ing.rista.diffr.*;
import it.unitn.ing.rista.util.Constants;


/**
 *  The ImageDataMask is a class to operate on images to remove
 *  bad pixels or area prior to integration
 *
 *
 * @version $Revision: 1.0 $, $Date: 2021/11/5 10:37:00 $
 * @author Luca Lutterotti
 * @since JDK1.8
 */


public class ImageDataMask extends it.unitn.ing.rista.diffr.DataMask {

	public ImageDataMask(XRDcat aobj, String alabel) {
		super(aobj, alabel);
		identifier = "no mask";
		IDlabel = "no mask";
	}

	public ImageDataMask(XRDcat aobj) {
		this(aobj, "No mask applied");
	}

	public ImageDataMask() {
		identifier = "no mask";
		IDlabel = "no mask";
	}

	public void filterData(ImagePlus imp) {
	}

//	public void notifyStringChanged(String source) {
//		notifyStringChanged(source, Constants.INTENSITY_CALIBRATION);
//	}

//	public void notifyObjectChanged(XRDcat source) {
//		notifyUpObjectChanged(source, Constants.INTENSITY_CALIBRATION);
//	}

}
