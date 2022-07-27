/*
 * @(#)PseudoVoigtPeak.java created 01/01/1997 Mesiano
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

package it.unitn.ing.rista.diffr;

import it.unitn.ing.fortran.*;
import it.unitn.ing.rista.util.*;

import java.io.PrintStream;
import java.io.IOException;

/**
 * The PseudoVoigtPeak is a class
 *
 * @author Luca Lutterotti
 * @version $Revision: 1.7 $, $Date: 2006/11/10 09:33:00 $
 * @since JDK1.1
 */

public class PseudoVoigtPeak extends basicPeak {

	static final double hwhmtobetac = Math.sqrt(Constants.PI);
	static final double hwhmtobetag = Constants.sqrtln2pi;

	static final double coeffeta[] = {0.00016875,
			0.89998,
			0.9348,
			-2.3387,
			6.6241,
			-8.4903,
			3.37};
	static final int numberetacoeff = 7;

	static final double coeffhwhm[] = {0.99995,
			-0.47373,
			0.0008722,
			0.83161,
			-1.7819,
			2.438,
			-1.0147};
	static final int numberhwhmcoeff = 7;

	static final double coeffbetac[] = {0.0,
			1.5044,
			-13.969,
			150.41,
			-832.55,
			2582.6,
			-4692.5,
			4957.7,
			-2818.9,
			666.72
	};
	static final int numberbetacoeff = 10;

	public PseudoVoigtPeak(double pos, boolean dspacingbase, boolean energyDispersive, double[] wave, double[] weight,
	                       Reflection reflex, int order) {
		super(pos, dspacingbase, energyDispersive, wave, weight, reflex, order);
	}

	public static double[] getHwhmEtaFromIntegralBeta(double[] betaf, double[] broadInst) {
		double betahc = 0.0;
		for (int ij = 0; ij < numberbetacoeff; ij++)
			betahc += coeffbetac[ij] * Math.pow(broadInst[0], (double) ij);
		double betahg = 1.0 - betahc;

		double hwhmconv = 0.0;
		for (int i = 0; i < numberhwhmcoeff; i++)
			hwhmconv += coeffhwhm[i] * Math.pow(betahc, (double) i);

		if (hwhmconv == 0.0)
			hwhmconv = 1.0E-9;
		betahc *= ((broadInst[1] / hwhmconv) * hwhmtobetac);
		betahg *= (broadInst[1] / (hwhmconv * hwhmtobetag));

		double betac = betahc + betaf[0];
		double betag = Math.sqrt(betaf[1] * betaf[1] + betahg * betahg);

		double hwhmc = betac / hwhmtobetac;
		double hwhmg = betag * hwhmtobetag;

		double[] hwhm_eta = new double[]{0, 0};

		double totalhwhm = hwhmg + hwhmc;
		double x = hwhmc / totalhwhm;

		for (int i = 0; i < numberetacoeff; i++)
			hwhm_eta[1] += coeffeta[i] * Math.pow(x, (double) i);
		for (int i = 0; i < numberhwhmcoeff; i++)
			hwhm_eta[0] += coeffhwhm[i] * Math.pow(x, (double) i);

		hwhm_eta[0] *= totalhwhm;

		return hwhm_eta;
	}


	public void computePeak(DiffrDataFile diffrDataFile, double[] expfit,
	                        Sample asample, Instrument ainstrument,
	                        PrintStream out, boolean logOutput, double cutoff,
	                        int computeTexture, int computeStrain,
	                        int computeFhkl, boolean leBailExtraction, int[] minmaxindex,
	                        boolean computeBroadening, boolean reverseX) {

		double[] hwhm_i, eta, const1, const2, wave;
		int[] minindex, maxindex;
			Phase aphase = getPhase();
			Reflection refl = getReflex();

			String phase_name = aphase.toXRDcatString();
			while (phase_name.length() < 20)
				phase_name += " ";
			phase_name = phase_name.substring(0, 20);
//    int dataindex = diffrDataFile.getIndex();
//    int datasetIndex = diffrDataFile.getDataFileSet().getIndex();

//    if (computeBroadening)
//        addInstrumentalBroadening(ainstrument.getInstrumentalBroadeningAt(getMeanPosition(), diffrDataFile));
//      addInstrumentalBroadening(refl.getInstBroadFactor(dataindex));

			int nrad = ainstrument.getRadiationType().getLinesCount();
			int totalLines = diffrDataFile.positionsPerPattern * nrad;
			double[] finalposition = new double[totalLines];
			double[][] intensity = new double[3][totalLines];
			hwhm_i = new double[totalLines];
			eta = new double[totalLines];
			double[][] actualPosition = new double[3][totalLines];
			const1 = new double[totalLines];
			const2 = new double[totalLines];
			wave = new double[nrad];
			minindex = new int[totalLines];
			maxindex = new int[totalLines];

			double[][][] positions = diffrDataFile.getPositions(aphase);
			double[][] absDetectorCorrection = new double[diffrDataFile.positionsPerPattern][nrad];
			int ipv = 0;
			for (int i = 0; i < nrad; i++) {
				double energy = Constants.ENERGY_LAMBDA / getRadiationWavelength(i) * 0.001;
				for (int j = 0; j < diffrDataFile.positionsPerPattern; j++) {
					double localPosition = positions[getOrderPosition()][j][i];
//					double strain = diffrDataFile.getStrains(aphase, getOrderPosition())[j][i];
//					localPosition = diffrDataFile.getPositionForStrained(localPosition, strain);
					int pointIndex = diffrDataFile.getOldNearestPoint(localPosition);
					absDetectorCorrection[j][i] = ainstrument.getDetector().getAbsorptionCorrection(diffrDataFile, pointIndex, energy);
					finalposition[ipv++] = localPosition;
				}
			}

		double Fhkl;
		switch (computeFhkl) {
			case Constants.COMPUTED:
				Fhkl = diffrDataFile.getDataFileSet().getStructureFactors(aphase)[1][getOrderPosition()];
				break;
			case Constants.EXPERIMENTAL:
				Fhkl = diffrDataFile.getDataFileSet().getStructureFactors(aphase)[0][getOrderPosition()];
				break;
			case Constants.UNITARY:
				Fhkl = 99.0;
				break;
			default:
				Fhkl = 88.0;
		}

//    Fhklist = new double[totalLines];
		double[] radiationWeight = new double[nrad];
		int principalRad = 0;
		radiationWeight[0] = getRadiationWeight(0);
		double weight = radiationWeight[0];
		for (int i1 = 1; i1 < nrad; i1++) {
			radiationWeight[i1] = getRadiationWeight(i1);
			if (leBailExtraction && weight < radiationWeight[i1]) {
				weight = radiationWeight[i1];
				principalRad = i1;
			}
		}

		double asyConst1 = aphase.getActivePlanarDefects().getPlanarDefectAsymmetryConstant1(getReflex());
		double asyConst2 = aphase.getActivePlanarDefects().getPlanarDefectAsymmetryConstant2(getReflex());
		double planar_asymmetry = aphase.getActivePlanarDefects().getPlanarDefectAsymmetry(getReflex());

		double[][][][] hwhm_eta = diffrDataFile.getBroadFactors(aphase);
		double[][][] deff = diffrDataFile.getCrystallitesMicrostrains(aphase)[0];

//	  for (int j = 0; j < diffrDataFile.positionsPerPattern; j++) {
			double[][] thwhm = hwhm_eta[0][getOrderPosition()];
			double[][] teta = hwhm_eta[1][getOrderPosition()];
//	  }
			double intensitySingle = getScaleFactor();
			double[][] textureFactor;

			switch (computeTexture) {
				case Constants.COMPUTED:
					textureFactor = diffrDataFile.getTextureFactors(aphase)[1][getOrderPosition()];
					break;
				case Constants.EXPERIMENTAL:
					textureFactor = diffrDataFile.getTextureFactors(aphase)[0][getOrderPosition()];
					break;
				case Constants.UNITARY:
				default:
					textureFactor = new double[diffrDataFile.positionsPerPattern][nrad];
					for (int i = 0; i < diffrDataFile.positionsPerPattern; i++)
						for (int j = 0; j < nrad; j++)
							textureFactor[i][j] = 1.0;
			}
			for (int i = 0; i < diffrDataFile.positionsPerPattern; i++)
				for (int j = 0; j < nrad; j++)
					if (Double.isNaN(textureFactor[i][j]))
						textureFactor[i][j] = 1.0;
//		if (getOrderPosition() == 2)
//	    System.out.println(diffrDataFile.getLabel() + ", texture factor: " + textureFactor[0]);
			double[][] shapeAbs = diffrDataFile.getShapeAbsFactors(aphase, getOrderPosition());
//	  System.out.println(" Total " + diffrDataFile.startingindex + " " + diffrDataFile.finalindex);
//	  System.out.println(" Range " + diffrDataFile.getXData(diffrDataFile.startingindex) + " " +
//			  diffrDataFile.getXData(diffrDataFile.finalindex));

			double[][] lorentzPolarization = diffrDataFile.getLorentzPolarization(aphase, getOrderPosition());
			ipv = 0;
			for (int i = 0; i < nrad; i++) {
				for (int j = 0; j < diffrDataFile.positionsPerPattern; j++) {
					if (radiationWeight[i] > 0.0) {
						double tmpIntensity = (intensitySingle * textureFactor[j][i] * shapeAbs[j][i] * Fhkl *
								radiationWeight[i] * aphase.getScaleFactor() * lorentzPolarization[j][i] * absDetectorCorrection[j][i]);
						if (const2[ipv] != 0.0) {
							for (int index = 0; index < 3; index++)
								intensity[index][ipv] = tmpIntensity * getReflex().pd_deltaIndex[index];
						} else {
							intensity[0][ipv] = tmpIntensity;
						}
						hwhm_i[ipv] = 1.0 / thwhm[j][i];
						eta[ipv] = teta[j][i];

						minindex[ipv] = diffrDataFile.getOldNearestPoint(finalposition[ipv] - thwhm[j][i] * cutoff);
						maxindex[ipv] = diffrDataFile.getOldNearestPoint(finalposition[ipv] + thwhm[j][i] * cutoff) + 1;

						if (!leBailExtraction || (leBailExtraction && i == principalRad)) {
							if (minmaxindex[0] > minindex[ipv])
								minmaxindex[0] = minindex[ipv];
							if (minmaxindex[1] < maxindex[ipv])
								minmaxindex[1] = maxindex[ipv];
						}

						const1[ipv] = asyConst1;
						const2[ipv] = asyConst2;
						wave[i] = getRadiationWavelength(i);

						if (const2[ipv] != 0.0) {
							for (int index = 0; index < 3; index++)
								actualPosition[index][ipv] = getPositionChangeForPlanarDefectDisplacement(finalposition[ipv], index);
						} else {
							actualPosition[0][ipv] = finalposition[ipv];
						}

						if (logOutput && out != null) {
							try {
							out.print(" ");
								out.print(getOrderPosition());
								out.print(" ");
								out.print(i);
								out.print(" " + phase_name);
								out.print(refl.getH());
								out.print(" ");
								out.print(refl.getK());
								out.print(" ");
								out.print(refl.getL());
								out.print(" ");
								out.print((float) refl.d_space);
								out.print(" ");
								out.print((float) diffrDataFile.getDataFileSet().getStructureFactors(aphase)[1][getOrderPosition()]);
								out.print(" ");
								out.print((float) diffrDataFile.getDataFileSet().getStructureFactors(aphase)[0][getOrderPosition()]);
								out.print(" ");
								out.print((float) actualPosition[0][j]);
								out.print(" ");
								out.print((float) diffrDataFile.getStrains(aphase, getOrderPosition())[j][i]);
								out.print(" ");
								out.print((float) refl.getPlanarDefectDisplacement(0));
								out.print(" ");
								out.print((float) intensity[0][ipv]);
								out.print(" ");
								out.print((float) thwhm[j][i]);
								out.print(" ");
								out.print((float) eta[ipv]);
								out.print(" ");
								out.print((float) Fhkl);
								out.print(" ");
								out.print((float) intensitySingle);
								out.print(" ");
								out.print((float) lorentzPolarization[j][i]);
								out.print(" ");
								out.print((float) textureFactor[j][i]);
								out.print(" ");
								out.print((float) shapeAbs[j][i]);
								out.print(" ");
								out.print((float) radiationWeight[i]);
								out.print(" ");
								out.print((float) aphase.getScaleFactor());

								out.print(Constants.lineSeparator);
								out.flush();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						intensity[0][ipv] = 0.0f;
//						Fhkl = 1.0f;
						hwhm_i[ipv] = 1.0f;
						eta[ipv] = 0.0f;
						actualPosition[0][ipv] = 0.0f;
						minindex[ipv] = 0;
						maxindex[ipv] = 4;
						const1[ipv] = 0.0f;
						const2[ipv] = 0.0f;
					}
					ipv++;
				}
			}

//    diffrDataFile.computeLorentzPolarization(ainstrument, asample, actualPosition, intensity);
// planar defects
/*		aphase.getActiveTDSModel().computeTDS(diffrDataFile, expfit, this, intensity[0], Fhkl, actualPosition[0], minmaxindex);*/
			computeFunctions(diffrDataFile.getXData(), expfit, minindex, maxindex,
					intensity, eta, hwhm_i, actualPosition, const1, const2, wave,
					diffrDataFile.dspacingbase, diffrDataFile.energyDispersive, diffrDataFile.increasingX(), planar_asymmetry,
					deff[0][0][0]);
/*	  computeFunctions(expfit, thwhm, cutoff, minindex, maxindex,
        intensity, eta, hwhm_i, actualPosition, const1, const2, wave,
        reverseX, minmaxindex, getOrderPosition(), aphase, diffrDataFile);*/

	}

	public void computeFunctions(double[] x, double[] f, int[] minindex, int[] maxindex,
	                                    double[][] intensity, double[] eta, double[] hwhm_i, double[][] position,
	                                    double[] const1, double[] const2, double[] wave, boolean dspacingBase,
	                                    boolean energyDispersive, boolean increasingX, double planar_asymmetry,
	                                    double deff) {

		int numberOfPV = minindex.length;
		double differenceDspace;
		for (int ipv = 0; ipv < numberOfPV; ipv++) {
			int imin = minindex[ipv];
			int imax = maxindex[ipv];
			if (imax - imin > 0) {
//				System.out.println(ipv + " " + planar_asymmetry + " " + const1[ipv] + " " + const2[ipv]);
				if (const2[ipv] != 0.0f) {
					for (int index = 0; index < 3; index++) {
						double dgx = intensity[index][ipv] * (1.0 - eta[ipv]) * Constants.sqrtln2pi * hwhm_i[ipv];
						double dcx = intensity[index][ipv] * eta[ipv] * hwhm_i[ipv] / Math.PI;
						for (int i = imin; i < imax; i++) {
							//			System.out.println("Peak: " + ipv + " " + x[i] + " " + position[ipv] + planar_asymmetry + " " + const1[ipv] + " " + const2[ipv]);
							double dx = x[i] - position[index][ipv];
							double dx_pi = dx;
							if (dspacingBase)
								differenceDspace = 1.0 / x[i] - 1.0 / position[ipv][index];
							else if (energyDispersive)
								differenceDspace = x[i] / Constants.ENERGY_LAMBDA - position[index][ipv] / Constants.ENERGY_LAMBDA;
							else {
								differenceDspace = 2.0 * (MoreMath.sind(x[i] * 0.5) - MoreMath.sind(position[index][ipv] * 0.5)) / wave[ipv];
//							System.out.println(differenceDspace / dx_pi);
							}
							double asy = 0.0;
							if (Math.abs(dx) > 1.0E-6) {
								double cxasy = const1[ipv] * deff * differenceDspace;
								double rasy = 1.0 + 1.0 / (cxasy * cxasy);
								asy = intensity[index][ipv] * const2[ipv] / (rasy * dx_pi);
//							System.out.println("Peak: " + ipv + " " + x[i] + " " + position[ipv] + " " + cxasy + " " + const1[ipv] + " " + const2[ipv] + " " + i + " " + asy);
							}
							dx *= hwhm_i[ipv];
							dx *= dx;
							if (dx > 30.0)
								f[i] += (float) (dcx / (1.0 + dx));
							else
								f[i] += (float) (dcx / (1.0 + dx) + dgx * Math.exp(-Constants.LN2 * dx));
							f[i] += asy;
						}
					}
				} else {
					double[] tmpFit = new double[imax - imin];
					double dgx = intensity[0][ipv] * (1.0 - eta[ipv]) * Constants.sqrtln2pi * hwhm_i[ipv];
					double dcx = intensity[0][ipv] * eta[ipv] * hwhm_i[ipv] / Math.PI;

					for (int i = imin; i < imax; i++) {
						double dx = position[0][ipv] - x[i];
						dx *= hwhm_i[ipv];
						dx *= dx;
						if (dx > 30.0)
							tmpFit[i - imin] += (float) (dcx / (1.0 + dx));
						else
							tmpFit[i - imin] += (float) (dcx / (1.0 + dx) + dgx * Math.exp(-Constants.LN2 * dx));
					}
					if (planar_asymmetry != 0.0) {
						double rec_planar_asymmetry = 1.0 / planar_asymmetry;
						double newFit[] = new double[imax - imin];
						int absdirection = -1;  // increasing step
						if (!increasingX)
							absdirection = -absdirection;
						for (int j = imin; j < imax; j++) {
							int direction = absdirection;
							double function = tmpFit[j - imin];
							double normalization = 1.0;
							int ij = j + direction;
							double difference;
							double expasymmetry = 1.0;
							for (; expasymmetry > 0.0001 && ij < imax && ij >= imin; ij += direction) {
								difference = Math.abs(x[ij] - x[j]);
								expasymmetry = Math.exp(-difference * rec_planar_asymmetry);
								function += tmpFit[ij - imin] * expasymmetry;
								normalization += expasymmetry;
							}
							newFit[j - imin] = function / normalization;
						}
						System.arraycopy(newFit, 0, tmpFit, 0, imax - imin);
					}
					for (int i = imin; i < imax; i++)
						f[i] += tmpFit[i - imin];
				}
			}
		}
	}

	public static void computeFunctions(double[] f, double[] thwhm, double cutoff, int[] minindex, int[] maxindex,
	                                    double[] intensity, double[] eta, double[] hwhm_i, double[] position,
	                                    double[] const1, double[] const2, double[] wave,
	                                    boolean reverseX, int[] minmaxindex,
	                                    int reflexIndex, Phase phase, DiffrDataFile diffrDataFile) {
		int numberOfPV = position.length;
		int i = 0, imin = 0, imax = 0;
		double dx = 0;
		int incrX = 1;
		if (reverseX)
			incrX = -1;
		boolean reversing = (reverseX && !diffrDataFile.dspacingbase) || (diffrDataFile.dspacingbase && !reverseX);
		double[] x = diffrDataFile.getXData();

		double differenceDspace, sinpos;
		int minIndex = diffrDataFile.startingindex;
		int maxIndex = diffrDataFile.finalindex;
		minmaxindex[0] = maxIndex;
		minmaxindex[1] = minIndex;

		int[][][][] minmaxIndex = diffrDataFile.getMinMaxIndices(phase);

		int numberRad = numberOfPV / diffrDataFile.positionsPerPattern;
		int ipv = 0;
		for (int nrad = 0; nrad < numberRad; nrad++) {
			double i_wave = 2.0 / wave[nrad];
			for (int ppp = 0; ppp < diffrDataFile.positionsPerPattern; ppp++) {
				double range = thwhm[ppp] * cutoff;

				double intconst = intensity[ipv] * const2[ipv];

				if (minmaxIndex[0][nrad][reflexIndex][ppp] < minIndex || minmaxIndex[0][nrad][reflexIndex][ppp] > maxIndex)
					minmaxIndex[0][nrad][reflexIndex][ppp] = minIndex;
				if (minmaxIndex[1][nrad][reflexIndex][ppp] < 0 || minmaxIndex[1][nrad][reflexIndex][ppp] > maxIndex)
					minmaxIndex[1][nrad][reflexIndex][ppp] = maxIndex;
//	    System.out.println(" Previous[" + ipv + "] " + refl.minIndex[datasetIndex][ipv] + " " + refl.maxIndex[datasetIndex][ipv]);

				if (reversing) {
					imin = minmaxIndex[1][nrad][reflexIndex][ppp];
					if (imin < minIndex)
						imin = minIndex;
//      System.out.println(ipv + " - " + position[ipv] + " " + x[minindex[ipv]]);
//        maxindex[ipv] = minindex[ipv];
					if (diffrDataFile.dspacingbase)
						while (imin < maxIndex - 1 && (x[imin] - position[ipv]) < range)
							imin++;
					else
						while (imin < maxIndex - 1 && (position[ipv] - x[imin]) < range)
							imin++;
					imax = minIndex - 1;
				} else {
					imin = minmaxIndex[0][nrad][reflexIndex][ppp];
					if (imin > maxIndex - 1)
						imin = maxIndex - 1;
//      System.out.println(ipv + " - " + position[ipv] + " " + x[minindex[ipv]]);
//        maxindex[ipv] = minindex[ipv];
					if (diffrDataFile.dspacingbase)
						while (imin > minIndex && (x[imin] - position[ipv]) < range)
							imin--;
					else
						while (imin > minIndex && (position[ipv] - x[imin]) < range)
							imin--;
					imax = maxIndex;
				}

				boolean entered = false;
				if (const2[ipv] != 0.0f) {
					double dgx = intensity[ipv] * (1.0 - eta[ipv]) * Constants.sqrtln2pi * hwhm_i[ipv];
					double dcx = intensity[ipv] * eta[ipv] * hwhm_i[ipv] / Math.PI;

					if (diffrDataFile.dspacingbase)
						sinpos = 1.0 / position[ipv];
					else if (diffrDataFile.energyDispersive)
						sinpos = position[ipv] * Constants.I_ENERGY_LAMBDA;
					else
						sinpos = MoreMath.sind(position[ipv] * 0.5);

					for (i = imin; i != imax; i += incrX) {
						dx = x[i] - position[ipv];
						if (Math.abs(dx) < range) {
							if (!entered) {
								entered = true;
								if (reversing)
									minmaxIndex[1][nrad][reflexIndex][ppp] = Math.min(i, maxIndex);
								else
									minmaxIndex[0][nrad][reflexIndex][ppp] = Math.max(i, minIndex);
							}
							if (diffrDataFile.dspacingbase)
								differenceDspace = 1.0 / x[i] - sinpos;
							else if (diffrDataFile.energyDispersive)
								differenceDspace = x[i] * Constants.I_ENERGY_LAMBDA - sinpos;
							else
								differenceDspace = (MoreMath.sind(x[i] * 0.5) - sinpos) * i_wave;

							double asy = 0.0;
							if (Math.abs(dx) > 1.0E-6) {
								asy = const1[ipv] * differenceDspace;
								asy = 1.0 + 1.0 / (asy * asy);
								asy = intconst / (asy * dx);
							}
							dx *= hwhm_i[ipv];
							dx *= dx;
							double dcx1 = dcx / (1.0 + dx);
							if (dx > 30.0)
								f[i] += dcx1;
							else
								f[i] += dcx1 + dgx * Math.exp(-Constants.LN2 * dx);
							f[i] += asy;
						} else if (entered) {
							break;
						}
					}

				} else {
					double dgx = intensity[ipv] * (1.0 - eta[ipv]) * Constants.sqrtln2pi * hwhm_i[ipv];
					double dcx = intensity[ipv] * eta[ipv] * hwhm_i[ipv] / Math.PI;

//	      System.out.println(" * " + imin + " " + imax + " " + position[ipv]);
					for (i = imin; i != imax; i += incrX) {
						dx = (position[ipv] - x[i]);
						if (Math.abs(dx) < range) {
							if (!entered) {
								entered = true;
//	            System.out.println("Entered: " + x[i]);
								if (reversing)
									minmaxIndex[1][nrad][reflexIndex][ppp] = Math.min(i, maxIndex);
								else
									minmaxIndex[0][nrad][reflexIndex][ppp] = Math.max(i, minIndex);
							}
							dx *= hwhm_i[ipv];
							dx *= dx;
							double dcx1 = dcx / (1.0 + dx);
							if (dx > 30.0)
								f[i] += dcx1;
							else
								f[i] += dcx1 + dgx * Math.exp(-Constants.LN2 * dx);
						} else if (entered) {
//	          System.out.println("Exited: " + x[i]);
							break;
						}
					}
				}
				if (reversing)
					minmaxIndex[0][nrad][reflexIndex][ppp] = Math.max(i, minIndex);
				else
					minmaxIndex[1][nrad][reflexIndex][ppp] = Math.min(i, maxIndex);
				if (minmaxindex[0] > minmaxIndex[0][nrad][reflexIndex][ppp])
					minmaxindex[0] = minmaxIndex[0][nrad][reflexIndex][ppp];
				if (minmaxindex[1] < minmaxIndex[1][nrad][reflexIndex][ppp])
					minmaxindex[1] = minmaxIndex[1][nrad][reflexIndex][ppp];
			}
			ipv++;
		}

		// check
		minmaxIndex = diffrDataFile.getMinMaxIndices(phase);
		for (int nrad = 0; nrad < numberRad; nrad++) {
			System.out.println(" New[" + nrad + "] " + minmaxIndex[0][nrad][reflexIndex][0] + " " + minmaxIndex[1][nrad][reflexIndex][0]);
		}
	}

}

