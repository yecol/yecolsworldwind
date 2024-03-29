/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.geom.*;

import java.util.List;

/**
 * An elevation model that always returns zero elevations.
 *
 * @author tag
 * @version $Id: ZeroElevationModel.java 13008 2010-01-14 21:03:42Z tgaskins $
 */
public class ZeroElevationModel extends AbstractElevationModel
{
    public double getMaxElevation()
    {
        return 1;
    }

    public double getMinElevation()
    {
        return 0;
    }

    public double[] getExtremeElevations(Angle latitude, Angle longitude)
    {
        return new double[] {0, 1};
    }

    public double[] getExtremeElevations(Sector sector)
    {
        return new double[] {0, 1};
    }

    public double getElevations(Sector sector, List<? extends LatLon> latlons, double targetResolution, double[] buffer)
    {
        for (int i = 0; i < latlons.size(); i++)
        {
            buffer[i++] = 0;
        }

        return 0;
    }

    @Override
    public double getUnmappedElevations(Sector sector, List<? extends LatLon> latlons, double targetResolution,
        double[] buffer)
    {
        return this.getElevations(sector, latlons, targetResolution, buffer);
    }

    public int intersects(Sector sector)
    {
        return 0;
    }

    public boolean contains(Angle latitude, Angle longitude)
    {
        return true;
    }

    @SuppressWarnings({"JavadocReference"})
    public double getBestResolution(Sector sector)
    {
        return 10;
    }

    public double getUnmappedElevation(Angle latitude, Angle longitude)
    {
        return 0;
    }
}
