using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Windows;
using System.Windows.Threading;
using System.Text;
using System.IO;

namespace AgentApp
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        void App_DispatcherUnhandledException(object sender, DispatcherUnhandledExceptionEventArgs e)
        {
            int i = 0;
            var cur = e.Exception;

            var sb = new StringBuilder();

            while (cur != null)
            {
                sb.AppendFormat("*** Level {0} ***", i);
                sb.AppendLine();


                sb.AppendLine(cur.Message);
                sb.AppendLine(cur.StackTrace);

                sb.AppendLine();
                sb.AppendLine();
                sb.AppendLine();

                i++;
                cur = cur.InnerException;
            }

            File.WriteAllText("error.txt", sb.ToString());
        }
    }
}
