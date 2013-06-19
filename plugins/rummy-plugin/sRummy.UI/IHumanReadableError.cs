using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Rummy;

namespace SRummy.UI
{
    public interface IHumanReadableError
    {
        IList<string> GetPossibleMessages(ErrorMessage error);
    }
}
