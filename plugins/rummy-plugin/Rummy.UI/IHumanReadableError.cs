using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy.UI
{
    public interface IHumanReadableError
    {
        IList<string> GetPossibleMessages(ErrorMessage error);
    }
}
