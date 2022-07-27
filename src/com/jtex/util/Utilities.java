/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jtex.util;

import com.jtex.arrays.Array1D;
import it.unitn.ing.rista.util.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author flb
 */
public class Utilities {

    public static double BesselI(double nu, double Z, boolean scaled) {

        double[] prec = new double[(int) nu + 1];
        int i = SpecFun.ribesl(Z, nu % 1, 1 + (scaled ? 1 : 0), prec);
        return prec[i - 1];
    }

    public static void main(String[] args) {

        double nu = 1.5;
        double z = 15;
        System.out.println(BesselI(nu, z, false));
        System.out.println(BesselI(nu, z, true));
        
        
        double[] X = new double[1000];
        double p = 40;
        SpecFun.ribesl(p, 0, 2, X);
        
        
        new Array1D(X).print();
        
    }

    private static final String appname = "maud"; //"JTex" + File.separator + "0.1";

    public static String getAppPath() {

        String env = Constants.startingAppDirectory;
/*        if (System.getenv("APPDATA") != null) {
            env = System.getenv("APPDATA") + File.separator + appname;
        } else {
            env = System.getenv("user.home") + File.separator + "." + appname;
        }*/

        return env;
    }

	public static void setPermission(File file, boolean executing) throws IOException {
    	try {
	      Set<PosixFilePermission> perms = new HashSet<>();
	      perms.add(PosixFilePermission.OWNER_READ);
	      perms.add(PosixFilePermission.OWNER_WRITE);
	      if (executing)
	         perms.add(PosixFilePermission.OWNER_EXECUTE);

	      perms.add(PosixFilePermission.OTHERS_READ);
	      perms.add(PosixFilePermission.OTHERS_WRITE);
	      if (executing)
	         perms.add(PosixFilePermission.OWNER_EXECUTE);

	      perms.add(PosixFilePermission.GROUP_READ);
	      perms.add(PosixFilePermission.GROUP_WRITE);
	      if (executing)
	         perms.add(PosixFilePermission.GROUP_EXECUTE);

	      Files.setPosixFilePermissions(file.toPath(), perms);
      } catch (Exception io) {
    		System.out.println("Setting permissions for file " + file.getAbsolutePath() + " failed!");
    		io.printStackTrace();
      }
	}

}
