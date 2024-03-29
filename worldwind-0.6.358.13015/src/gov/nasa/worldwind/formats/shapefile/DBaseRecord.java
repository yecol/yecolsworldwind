/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.formats.shapefile;

import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.util.Logging;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * @author Patrick Murris
 * @version $Id: DBaseRecord.java 12797 2009-11-16 04:40:34Z patrickmurris $
 */
public class DBaseRecord extends AVListImpl
{
    private boolean deleted = false;
    private int recordNumber;

    public boolean isDeleted()
    {
        return this.deleted;
    }

    public int getRecordNumber()
    {
        return this.recordNumber;
    }

    protected static DBaseRecord fromBuffer(DBaseFile dbf, ByteBuffer buffer, int recordNumber)
    {
        DBaseRecord record = new DBaseRecord();
        DBaseField[] fields = dbf.getFields();

        // Record number
        record.recordNumber = recordNumber;

        // Deleted record flag
        byte deleted = buffer.get();
        record.deleted = deleted == 0x2A;

        // Read fields
        for (DBaseField field : fields)
        {
            String value = DBaseFile.readZeroTerminatedString(buffer, field.getLength());
            value = value.trim();

            try
            {
                if (field.getType().equals(DBaseField.TYPE_BOOLEAN))
                {
                    value = value.toUpperCase();
                    record.setValue(field.getName(), value.equals("T") || value.equals("Y"));
                }
                else if (field.getType().equals(DBaseField.TYPE_CHAR))
                {
                    record.setValue(field.getName(), value);
                }
                else if (field.getType().equals(DBaseField.TYPE_DATE))
                {
                    int year = Integer.parseInt(value.substring(0, 4));
                    int month = Integer.parseInt(value.substring(4, 6));
                    int day = Integer.parseInt(value.substring(6, 8));

                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month - 1, day);
                    record.setValue(field.getName(), cal.getTime());
                }
                else if (field.getType().equals(DBaseField.TYPE_NUMBER))
                {
                    Number n = Double.parseDouble(value);
                    record.setValue(field.getName(), field.getDecimals() == 0 ? n.intValue() : n.doubleValue());
                }
            }
            catch (Exception e)
            {
                // Log warning but keep reading
                String message = Logging.getMessage("generic.ConversionError", value);
                Logging.logger().log(java.util.logging.Level.WARNING, message, e);
            }
        }

        return record;
    }
}
