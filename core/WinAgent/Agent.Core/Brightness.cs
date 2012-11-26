using System;
using System.Text;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Management;

namespace Agent.Core
{
    /// <summary>
    /// Class for manipulating the brightness of the screen
    /// </summary>
    public static class Brightness
    {
        static void SetBrightness(byte targetBrightness)
        {
            //define scope (namespace)
            var x = new ManagementScope("root\\WMI");

            //define query
            var q = new SelectQuery("WmiMonitorBrightnessMethods");

            //output current brightness
            using (var mox = new ManagementObjectSearcher(x, q))
            {
                using (var mok = mox.Get())
                {
                    try
                    {
                        foreach (ManagementObject o in mok)
                        {
                            o.InvokeMethod("WmiSetBrightness", new Object[] { UInt32.MaxValue, targetBrightness }); //note the reversed order - won't work otherwise!
                            break; //only work on the first object
                        }
                    }
                    catch (ManagementException ex)
                    {
                        Console.WriteLine("Setting brightness failed: ", ex.Message);
                    }
                }
            }
        }

        public static void SetHighestBrightness()
        {
           SetBrightness(100);
        }

        public static void SetLowestBrightness()
        {
            SetBrightness(0);
        }
    }
}
