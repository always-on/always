using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

// A set of extension methods for determining string similarity
// Collected, refactored, or ported by Steve Hawley, steve.hawley@atalasoft.com
// Credit for original code is attached to the public method
// Use freely, but at your own risk - not guaranteed to be correct


namespace SoundsLikeExtensions
{
    public static class SoundsLike
    {
        static SoundsLike()
        {
            _soundExTable = new List<KeyValuePair<string,string>>();
            _soundExTable.Add(new KeyValuePair<string,string>("bfpv", "1"));
            _soundExTable.Add(new KeyValuePair<string,string>("cgjkqsxz", "2"));
            _soundExTable.Add(new KeyValuePair<string,string>("dt", "3"));
            _soundExTable.Add(new KeyValuePair<string,string>("l", "4"));
            _soundExTable.Add(new KeyValuePair<string,string>("mn", "5"));
            _soundExTable.Add(new KeyValuePair<string,string>("r", "6"));
        }


        // Extension method for getting Levenshtein distance, based on:
        // http://www.merriampark.com/ldcsharp.htm
        // by Lasse Johansen
        public static int LevenshteinDistance(this string s, string t)
        {
            if (s == null)
                throw new ArgumentNullException("s");
            if (t == null)
                throw new ArgumentNullException("t");
            int n = s.Length;
            int m = t.Length;
            int[,] d = new int[n+1,m+1];

            if (n == 0 || m == 0)
                return Math.Max(m, n);

            for (int i = 0; i <= n; i++)
            {
                d[i, 0] = i;
            }
            for (int i = 0; i < m; i++)
            {
                d[0, i] = i;
            }

            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < m; j++)
                {
                    int cost = (t[j] == s[i]) ? 0 : 1;

                    d[i + 1, j + 1] = Math.Min(Math.Min(d[i, j + 1] + 1, d[i + 1, j] + 1), d[i, j] + cost);
                }
            }

            return d[n, m];
        }

        // Extension method for getting SoundEx, based on:
        // http://blogs.techrepublic.com.com/programming-and-development/?p=656
        // by Zach Smith

        static List<KeyValuePair<string, string>> _soundExTable;
         
        public static string SoundEx(this string s)
        {
            if (s == null)
                throw new ArgumentNullException("s");
            if (s.Length == 0)
                return "0000";

            StringBuilder sb = new StringBuilder();
            string previousCode = "";
            sb.Append(Char.ToLower(s[0]));

            for (int i=1; i < s.Length; i++)
            {
                char currentLetter = Char.ToLower(s[i]);
                
                KeyValuePair<string, string> found = _soundExTable.FirstOrDefault(kvp => kvp.Key.Contains(currentLetter));
                string currentCode = found.Value;
                if (currentCode != null) {
                    if (currentCode != previousCode) {
                        sb.Append(currentCode);
                        if (sb.Length == 4)
                            break;
                        previousCode = currentCode;
                    }
                }
            }
            if (sb.Length < 4)
                sb.Append(new String('0', 4 - sb.Length));
            return sb.ToString().ToUpper();
        }

        private static bool ContainsSubstring(this string s, string t, int start, int count)
        {
            if (s == null)
                throw new ArgumentNullException("s");
            if (t == null)
                throw new ArgumentNullException("t");
            return s.Contains(t.Substring(start, count));
        }

        public static int SoundExDifference(string soundex1, string soundex2)
        {
            if (soundex1 == null)
                throw new ArgumentNullException("soundex1");
            if (soundex2 == null)
                throw new ArgumentNullException("soundex2");
            if (soundex1.Length != 4)
                throw new ArgumentOutOfRangeException("soundex1", "soundex strings need to be 4 characters long");
            if (soundex2.Length != 4)
                throw new ArgumentOutOfRangeException("soundex2", "soundex strings need to be 4 characters long");
            
            int result = 0;

            if (soundex2.ContainsSubstring(soundex1, 1, 3))
            {
                result = 3;
            }
            else if (soundex2.ContainsSubstring(soundex1, 2, 2))
            {
                result = 2;
            }
            else if (soundex2.ContainsSubstring(soundex1, 1, 2))
            {
                result = 2;
            }
            else
            {
                if (soundex2.ContainsSubstring(soundex1, 1, 1))
                    result++;
                if (soundex2.ContainsSubstring(soundex1, 2, 1))
                    result++;
                if (soundex2.ContainsSubstring(soundex1, 3, 1))
                    result++;
            }
            if (soundex1[0] == soundex2[0])
                result++;
            return result == 0 ? 1 : result;
        }

        public static int SoundEx(this string s, string t)
        {
            return SoundExDifference(s.SoundEx(), t.SoundEx());
        }


        public static bool StartsWith(this string s, params string[] candidate)
        {
            string match = candidate.FirstOrDefault(t => s.StartsWith(t));
            return match != default(string);
        }

        public static bool SubstringIs(this string s, int start, int length, params string[] candidate)
        {
            if (start < 0)
                return false;
            string sub = s.Substring(start, length);
            string match = candidate.FirstOrDefault(t => t == sub);
            return match != default(string);
        }

        public static bool IsVowelOrY(this char c)
        {
            return "AEIOUY".Contains(Char.ToUpper(c));
        }

        static string[] _slavoGermanicRoots = new string[] { "W", "K", "CZ", "WITZ" };
        private static bool IsSlavoGermanic(string s)
        {
            return _slavoGermanicRoots.FirstOrDefault(t => s.Contains(t)) != default(string);
        }

        private static void AddMetaphoneCharacter(String primaryCharacter, String alternateCharacter, StringBuilder primaryKey, StringBuilder alternateKey)
        {
            //Is the primary character valid?
            if (primaryCharacter.Length > 0)
            {
                primaryKey.Append(primaryCharacter);
            }

            //Is the alternate character valid?
            if (alternateCharacter != null)
            {
                //Alternate character was provided.  If it is not zero-length, append it, else
                //append the primary string as long as it wasn't zero length and isn't a space character
                if (alternateCharacter.Length > 0)
                {
                    if (alternateCharacter[0] != ' ')
                    {
                        alternateKey.Append(alternateCharacter);
                    }
                }
                else
                {
                    //No, but if the primary character is valid, add that instead
                    if (primaryCharacter.Length > 0 && (primaryCharacter[0] != ' '))
                    {
                        alternateKey.Append(primaryCharacter);
                    }
                }
            }
            else if (primaryCharacter.Length > 0)
            {
                //Else, no alternate character was passed, but a primary was, so append the primary character to the alternate key
                alternateKey.Append(primaryCharacter);
            }
        }

        private const int METAPHONE_KEY_LENGTH = 4;


        // double metaphone from Adam Neslon here
        // http://www.codeproject.com/KB/recipes/dmetaphone5.aspx

        public static KeyValuePair<string, string> DoubleMetaphone(this string s)
        {
            if (s == null)
                throw new ArgumentNullException("s");
            string word = s.ToUpper();
            int length = word.Length;
            int last = length - 1;
            int current = 0;

            StringBuilder primaryKey = new StringBuilder();
            StringBuilder alternateKey = new StringBuilder();
            bool isSlavOrGermanic = IsSlavoGermanic(word);

            if (length < 1)
                return new KeyValuePair<string,string>("", "");

            //skip these when at start of word
            if (s.StartsWith("GN", "KN", "PN", "WR", "PS"))
                current += 1;

            //Initial 'X' is pronounced 'Z' e.g. 'Xavier'
            if (word[0] == 'X')
            {
                AddMetaphoneCharacter("S", null, primaryKey, alternateKey);	//'Z' maps to 'S'
                current += 1;
            }

            ///////////main loop//////////////////////////
            while ((primaryKey.Length < METAPHONE_KEY_LENGTH) || (alternateKey.Length < METAPHONE_KEY_LENGTH))
            {
                if (current >= length)
                    break;

                switch (word[current])
                {
                    case 'A':
                    case 'E':
                    case 'I':
                    case 'O':
                    case 'U':
                    case 'Y':
                        if (current == 0)
                            //all init vowels now map to 'A'
                            AddMetaphoneCharacter("A", null, primaryKey, alternateKey);
                        current++;
                        break;

                    case 'B':

                        //"-mb", e.g", "dumb", already skipped over...
                        AddMetaphoneCharacter("P", null, primaryKey, alternateKey);

                        if (word[current + 1] == 'B')
                            current += 2;
                        else
                            current++;
                        break;

                    case 'Ç':
                        AddMetaphoneCharacter("S", null, primaryKey, alternateKey);
                        current += 1;
                        break;

                    case 'C':
                        //various germanic
                        if ((current > 1)
                            && !word[current - 2].IsVowelOrY()
                            && word.SubstringIs((current - 1), 3, "ACH")
                            && ((word[current + 2] != 'I') && ((word[current + 2] != 'E')
                                                                  || word.SubstringIs((current - 2), 6, "BACHER", "MACHER"))))
                        {
                            AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //special case 'caesar'
                        if ((current == 0) && word.SubstringIs(current, 6, "CAESAR"))
                        {
                            AddMetaphoneCharacter("S", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //italian 'chianti'
                        if (word.SubstringIs(current, 4, "CHIA"))
                        {
                            AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        if (word.SubstringIs(current, 2, "CH"))
                        {
                            //find 'michael'
                            if ((current > 0) && word.SubstringIs(current, 4, "CHAE"))
                            {
                                AddMetaphoneCharacter("K", "X", primaryKey, alternateKey);
                                current += 2;
                                break;
                            }

                            //greek roots e.g. 'chemistry', 'chorus'
                            if ((current == 0)
                                && (word.SubstringIs((current + 1), 5, "HARAC", "HARIS")
                                     || word.SubstringIs((current + 1), 3, "HOR", "HYM", "HIA", "HEM"))
                                && !word.SubstringIs(0, 5, "CHORE"))
                            {
                                AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                                current += 2;
                                break;
                            }

                            //germanic, greek, or otherwise 'ch' for 'kh' sound
                            if ((word.SubstringIs(0, 4, "VAN ", "VON ") || word.SubstringIs(0, 3, "SCH"))
                                // 'architect but not 'arch', 'orchestra', 'orchid'
                                || word.SubstringIs((current - 2), 6, "ORCHES", "ARCHIT", "ORCHID")
                                || word.SubstringIs((current + 2), 1, "T", "S")
                                || ((word.SubstringIs((current - 1), 1, "A", "O", "U", "E") || (current == 0))
                                //e.g., 'wachtler', 'wechsler', but not 'tichner'
                                    && word.SubstringIs((current + 2), 1, "L", "R", "N", "M", "B", "H", "F", "V", "W", " ")))
                            {
                                AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                            }
                            else
                            {
                                if (current > 0)
                                {
                                    if (word.SubstringIs(0, 2, "MC"))
                                        //e.g., "McHugh"
                                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                                    else
                                        AddMetaphoneCharacter("X", "K", primaryKey, alternateKey);
                                }
                                else
                                    AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                            }
                            current += 2;
                            break;
                        }
                        //e.g, 'czerny'
                        if (word.SubstringIs(current, 2, "CZ") && !word.SubstringIs((current - 2), 4, "WICZ"))
                        {
                            AddMetaphoneCharacter("S", "X", primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //e.g., 'focaccia'
                        if (word.SubstringIs((current + 1), 3, "CIA"))
                        {
                            AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                            current += 3;
                            break;
                        }

                        //double 'C', but not if e.g. 'McClellan'
                        if (word.SubstringIs(current, 2, "CC") && !((current == 1) && (word[0] == 'M')))
                            //'bellocchio' but not 'bacchus'
                            if (word.SubstringIs((current + 2), 1, "I", "E", "H") && !word.SubstringIs((current + 2), 2, "HU"))
                            {
                                //'accident', 'accede' 'succeed'
                                if (((current == 1) && (word[current - 1] == 'A'))
                                    || word.SubstringIs((current - 1), 5, "UCCEE", "UCCES"))
                                    AddMetaphoneCharacter("KS", null, primaryKey, alternateKey);
                                //'bacci', 'bertucci', other italian
                                else
                                    AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                                current += 3;
                                break;
                            }
                            else
                            {//Pierce's rule
                                AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                                current += 2;
                                break;
                            }

                        if (word.SubstringIs(current, 2, "CK", "CG", "CQ"))
                        {
                            AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        if (word.SubstringIs(current, 2, "CI", "CE", "CY"))
                        {
                            //italian vs. english
                            if (word.SubstringIs(current, 3, "CIO", "CIE", "CIA"))
                                AddMetaphoneCharacter("S", "X", primaryKey, alternateKey);
                            else
                                AddMetaphoneCharacter("S", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //else
                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);

                        //name sent in 'mac caffrey', 'mac gregor
                        if (word.SubstringIs((current + 1), 2, " C", " Q", " G"))
                            current += 3;
                        else
                            if (word.SubstringIs((current + 1), 1, "C", "K", "Q")
                                && !word.SubstringIs((current + 1), 2, "CE", "CI"))
                                current += 2;
                            else
                                current += 1;
                        break;

                    case 'D':
                        if (word.SubstringIs(current, 2, "DG"))
                            if (word.SubstringIs((current + 2), 1, "I", "E", "Y"))
                            {
                                //e.g. 'edge'
                                AddMetaphoneCharacter("J", null, primaryKey, alternateKey);
                                current += 3;
                                break;
                            }
                            else
                            {
                                //e.g. 'edgar'
                                AddMetaphoneCharacter("TK", null, primaryKey, alternateKey);
                                current += 2;
                                break;
                            }

                        if (word.SubstringIs(current, 2, "DT", "DD"))
                        {
                            AddMetaphoneCharacter("T", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //else
                        AddMetaphoneCharacter("T", null, primaryKey, alternateKey);
                        current += 1;
                        break;

                    case 'F':
                        if (word[current + 1] == 'F')
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("F", null, primaryKey, alternateKey);
                        break;

                    case 'G':
                        if (word[current + 1] == 'H')
                        {
                            if ((current > 0) && !word[current - 1].IsVowelOrY())
                            {
                                AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                                current += 2;
                                break;
                            }

                            if (current < 3)
                            {
                                //'ghislane', ghiradelli
                                if (current == 0)
                                {
                                    if (word[current + 2] == 'I')
                                        AddMetaphoneCharacter("J", null, primaryKey, alternateKey);
                                    else
                                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                                    current += 2;
                                    break;
                                }
                            }
                            //Parker's rule (with some further refinements) - e.g., 'hugh'
                            if (((current > 1) && word.SubstringIs((current - 2), 1, "B", "H", "D"))
                                //e.g., 'bough'
                                || ((current > 2) && word.SubstringIs((current - 3), 1, "B", "H", "D"))
                                //e.g., 'broughton'
                                || ((current > 3) && word.SubstringIs((current - 4), 1, "B", "H")))
                            {
                                current += 2;
                                break;
                            }
                            else
                            {
                                //e.g., 'laugh', 'McLaughlin', 'cough', 'gough', 'rough', 'tough'
                                if ((current > 2)
                                    && (word[current - 1] == 'U')
                                    && word.SubstringIs((current - 3), 1, "C", "G", "L", "R", "T"))
                                {
                                    AddMetaphoneCharacter("F", null, primaryKey, alternateKey);
                                }
                                else
                                    if ((current > 0) && word[current - 1] != 'I')
                                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);

                                current += 2;
                                break;
                            }
                        }

                        if (word[current + 1] == 'N')
                        {
                            if ((current == 1) && word[0].IsVowelOrY() && !isSlavOrGermanic)
                            {
                                AddMetaphoneCharacter("KN", "N", primaryKey, alternateKey);
                            }
                            else
                                //not e.g. 'cagney'
                                if (!word.SubstringIs((current + 2), 2, "EY")
                                    && (word[current + 1] != 'Y') && !isSlavOrGermanic)
                                {
                                    AddMetaphoneCharacter("N", "KN", primaryKey, alternateKey);
                                }
                                else
                                    AddMetaphoneCharacter("KN", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //'tagliaro'
                        if (word.SubstringIs((current + 1), 2, "LI") && !isSlavOrGermanic)
                        {
                            AddMetaphoneCharacter("KL", "L", primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //-ges-,-gep-,-gel-, -gie- at beginning
                        if ((current == 0)
                            && ((word[current + 1] == 'Y')
                                 || word.SubstringIs((current + 1), 2, "ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER")))
                        {
                            AddMetaphoneCharacter("K", "J", primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        // -ger-,  -gy-
                        if ((word.SubstringIs((current + 1), 2, "ER") || (word[current + 1] == 'Y'))
                            && !word.StartsWith("DANGER", "RANGER", "MANGER")
                            && !word.SubstringIs((current - 1), 1, "E", "I")
                            && !word.SubstringIs((current - 1), 3, "RGY", "OGY"))
                        {
                            AddMetaphoneCharacter("K", "J", primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        // italian e.g, 'biaggi'
                        if (word.SubstringIs((current + 1), 1, "E", "I", "Y") || word.SubstringIs((current - 1), 4, "AGGI", "OGGI"))
                        {
                            //obvious germanic
                            if ((word.StartsWith("VAN ", "VON ", "SCH"))
                                || word.SubstringIs((current + 1), 2, "ET"))
                                AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                            else
                                //always soft if french ending
                                if (word.SubstringIs((current + 1), 4, "IER "))
                                    AddMetaphoneCharacter("J", null, primaryKey, alternateKey);
                                else
                                    AddMetaphoneCharacter("J", "K", primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        if (word[current + 1] == 'G')
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                        break;

                    case 'H':
                        //only keep if first & before vowel or btw. 2 vowels
                        if (((current == 0) || word[current - 1].IsVowelOrY())
                            && word[current + 1].IsVowelOrY())
                        {
                            AddMetaphoneCharacter("H", null, primaryKey, alternateKey);
                            current += 2;
                        }
                        else//also takes care of 'HH'
                            current += 1;
                        break;

                    case 'J':
                        //obvious spanish, 'jose', 'san jacinto'
                        if (word.SubstringIs(current, 4, "JOSE") || word.StartsWith("SAN "))
                        {
                            if (((current == 0) && (word[current + 4] == ' ')) || word.StartsWith("SAN "))
                                AddMetaphoneCharacter("H", null, primaryKey, alternateKey);
                            else
                            {
                                AddMetaphoneCharacter("J", "H", primaryKey, alternateKey);
                            }
                            current += 1;
                            break;
                        }

                        if ((current == 0) && !word.SubstringIs(current, 4, "JOSE"))
                            AddMetaphoneCharacter("J", "A", primaryKey, alternateKey);//Yankelovich/Jankelowicz
                        else
                            //spanish pron. of e.g. 'bajador'
                            if (word[current - 1].IsVowelOrY()
                                && !isSlavOrGermanic
                                && ((word[current + 1] == 'A') || (word[current + 1] == 'O')))
                                AddMetaphoneCharacter("J", "H", primaryKey, alternateKey);
                            else
                                if (current == last)
                                    AddMetaphoneCharacter("J", " ", primaryKey, alternateKey);
                                else
                                    if (!word.SubstringIs((current + 1), 1, "L", "T", "K", "S", "N", "M", "B", "Z")
                                        && !word.SubstringIs((current - 1), 1, "S", "K", "L"))
                                        AddMetaphoneCharacter("J", null, primaryKey, alternateKey);

                        if (word[current + 1] == 'J')//it could happen!
                            current += 2;
                        else
                            current += 1;
                        break;

                    case 'K':
                        if (word[current + 1] == 'K')
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                        break;

                    case 'L':
                        if (word[current + 1] == 'L')
                        {
                            //spanish e.g. 'cabrillo', 'gallegos'
                            if (((current == (length - 3))
                                 && word.SubstringIs((current - 1), 4, "ILLO", "ILLA", "ALLE"))
                                || ((word.SubstringIs((last - 1), 2, "AS", "OS") || word.SubstringIs(last, 1, "A", "O"))
                                    && word.SubstringIs((current - 1), 4, "ALLE")))
                            {
                                AddMetaphoneCharacter("L", " ", primaryKey, alternateKey);
                                current += 2;
                                break;
                            }
                            current += 2;
                        }
                        else
                            current += 1;
                        AddMetaphoneCharacter("L", null, primaryKey, alternateKey);
                        break;

                    case 'M':
                        if ((word.SubstringIs((current - 1), 3, "UMB")
                             && (((current + 1) == last) || word.SubstringIs((current + 2), 2, "ER")))
                            //'dumb','thumb'
                            || (word[current + 1] == 'M'))
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("M", null, primaryKey, alternateKey);
                        break;

                    case 'N':
                        if (word[current + 1] == 'N')
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("N", null, primaryKey, alternateKey);
                        break;

                    case 'Ñ':
                        current += 1;
                        AddMetaphoneCharacter("N", null, primaryKey, alternateKey);
                        break;

                    case 'P':
                        if (word[current + 1] == 'H')
                        {
                            AddMetaphoneCharacter("F", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //also account for "campbell", "raspberry"
                        if (word.SubstringIs((current + 1), 1, "P", "B"))
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("P", null, primaryKey, alternateKey);
                        break;

                    case 'Q':
                        if (word[current + 1] == 'Q')
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("K", null, primaryKey, alternateKey);
                        break;

                    case 'R':
                        //french e.g. 'rogier', but exclude 'hochmeier'
                        if ((current == last)
                            && !isSlavOrGermanic
                            && word.SubstringIs((current - 2), 2, "IE")
                            && !word.SubstringIs((current - 4), 2, "ME", "MA"))
                            AddMetaphoneCharacter("", "R", primaryKey, alternateKey);
                        else
                            AddMetaphoneCharacter("R", null, primaryKey, alternateKey);

                        if (word[current + 1] == 'R')
                            current += 2;
                        else
                            current += 1;
                        break;

                    case 'S':
                        //special cases 'island', 'isle', 'carlisle', 'carlysle'
                        if (word.SubstringIs((current - 1), 3, "ISL", "YSL"))
                        {
                            current += 1;
                            break;
                        }

                        //special case 'sugar-'
                        if ((current == 0) && word.StartsWith("SUGAR"))
                        {
                            AddMetaphoneCharacter("X", "S", primaryKey, alternateKey);
                            current += 1;
                            break;
                        }

                        if (word.SubstringIs(current, 2, "SH"))
                        {
                            //germanic
                            if (word.SubstringIs((current + 1), 4, "HEIM", "HOEK", "HOLM", "HOLZ"))
                                AddMetaphoneCharacter("S", null, primaryKey, alternateKey);
                            else
                                AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        //italian & armenian
                        if (word.SubstringIs(current, 3, "SIO", "SIA") || word.SubstringIs(current, 4, "SIAN"))
                        {
                            if (!isSlavOrGermanic)
                                AddMetaphoneCharacter("S", "X", primaryKey, alternateKey);
                            else
                                AddMetaphoneCharacter("S", null, primaryKey, alternateKey);
                            current += 3;
                            break;
                        }

                        //german & anglicisations, e.g. 'smith' match 'schmidt', 'snider' match 'schneider'
                        //also, -sz- in slavic language altho in hungarian it is pronounced 's'
                        if (((current == 0)
                             && word.SubstringIs((current + 1), 1, "M", "N", "L", "W"))
                            || word.SubstringIs((current + 1), 1, "Z"))
                        {
                            AddMetaphoneCharacter("S", "X", primaryKey, alternateKey);
                            if (word.SubstringIs((current + 1), 1, "Z"))
                                current += 2;
                            else
                                current += 1;
                            break;
                        }

                        if (word.SubstringIs(current, 2, "SC"))
                        {
                            //Schlesinger's rule
                            if (word[current + 2] == 'H')
                                //dutch origin, e.g. 'school', 'schooner'
                                if (word.SubstringIs((current + 3), 2, "OO", "ER", "EN", "UY", "ED", "EM"))
                                {
                                    //'schermerhorn', 'schenker'
                                    if (word.SubstringIs((current + 3), 2, "ER", "EN"))
                                    {
                                        AddMetaphoneCharacter("X", "SK", primaryKey, alternateKey);
                                    }
                                    else
                                        AddMetaphoneCharacter("SK", null, primaryKey, alternateKey);
                                    current += 3;
                                    break;
                                }
                                else
                                {
                                    if ((current == 0) && !word[3].IsVowelOrY() && (word[3] != 'W'))
                                        AddMetaphoneCharacter("X", "S", primaryKey, alternateKey);
                                    else
                                        AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                                    current += 3;
                                    break;
                                }

                            if (word.SubstringIs((current + 2), 1, "I", "E", "Y"))
                            {
                                AddMetaphoneCharacter("S", null, primaryKey, alternateKey);
                                current += 3;
                                break;
                            }
                            //else
                            AddMetaphoneCharacter("SK", null, primaryKey, alternateKey);
                            current += 3;
                            break;
                        }

                        //french e.g. 'resnais', 'artois'
                        if ((current == last) && word.SubstringIs((current - 2), 2, "AI", "OI"))
                            AddMetaphoneCharacter("", "S", primaryKey, alternateKey);
                        else
                            AddMetaphoneCharacter("S", null, primaryKey, alternateKey);

                        if (word.SubstringIs((current + 1), 1, "S", "Z"))
                            current += 2;
                        else
                            current += 1;
                        break;

                    case 'T':
                        if (word.SubstringIs(current, 4, "TION"))
                        {
                            AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                            current += 3;
                            break;
                        }

                        if (word.SubstringIs(current, 3, "TIA", "TCH"))
                        {
                            AddMetaphoneCharacter("X", null, primaryKey, alternateKey);
                            current += 3;
                            break;
                        }

                        if (word.SubstringIs(current, 2, "TH")
                            || word.SubstringIs(current, 3, "TTH"))
                        {
                            //special case 'thomas', 'thames' or germanic
                            if (word.SubstringIs((current + 2), 2, "OM", "AM")
                                || word.SubstringIs(0, 4, "VAN ", "VON ")
                                || word.SubstringIs(0, 3, "SCH"))
                            {
                                AddMetaphoneCharacter("T", null, primaryKey, alternateKey);
                            }
                            else
                            {
                                AddMetaphoneCharacter("0", "T", primaryKey, alternateKey);
                            }
                            current += 2;
                            break;
                        }

                        if (word.SubstringIs((current + 1), 1, "T", "D"))
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("T", null, primaryKey, alternateKey);
                        break;

                    case 'V':
                        if (word[current + 1] == 'V')
                            current += 2;
                        else
                            current += 1;
                        AddMetaphoneCharacter("F", null, primaryKey, alternateKey);
                        break;

                    case 'W':
                        //can also be in middle of word
                        if (word.SubstringIs(current, 2, "WR"))
                        {
                            AddMetaphoneCharacter("R", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }

                        if ((current == 0)
                            && (word[current + 1].IsVowelOrY() || word.SubstringIs(current, 2, "WH")))
                        {
                            //Wasserman should match Vasserman
                            if (word[current + 1].IsVowelOrY())
                                AddMetaphoneCharacter("A", "F", primaryKey, alternateKey);
                            else
                                //need Uomo to match Womo
                                AddMetaphoneCharacter("A", null, primaryKey, alternateKey);
                        }

                        //Arnow should match Arnoff
                        if (((current == last) && word[current - 1].IsVowelOrY())
                            || word.SubstringIs((current - 1), 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY")
                            || word.SubstringIs(0, 3, "SCH"))
                        {
                            AddMetaphoneCharacter("", "F", primaryKey, alternateKey);
                            current += 1;
                            break;
                        }

                        //polish e.g. 'filipowicz'
                        if (word.SubstringIs(current, 4, "WICZ", "WITZ"))
                        {
                            AddMetaphoneCharacter("TS", "FX", primaryKey, alternateKey);
                            current += 4;
                            break;
                        }

                        //else skip it
                        current += 1;
                        break;

                    case 'X':
                        //french e.g. breaux
                        if (!((current == last)
                              && (word.SubstringIs((current - 3), 3, "IAU", "EAU")
                                   || word.SubstringIs((current - 2), 2, "AU", "OU"))))
                            AddMetaphoneCharacter("KS", null, primaryKey, alternateKey);

                        if (word.SubstringIs((current + 1), 1, "C", "X"))
                            current += 2;
                        else
                            current += 1;
                        break;

                    case 'Z':
                        //chinese pinyin e.g. 'zhao'
                        if (word[current + 1] == 'H')
                        {
                            AddMetaphoneCharacter("J", null, primaryKey, alternateKey);
                            current += 2;
                            break;
                        }
                        else
                            if (word.SubstringIs((current + 1), 2, "ZO", "ZI", "ZA")
                                || (isSlavOrGermanic && ((current > 0) && word[current - 1] != 'T')))
                            {
                                AddMetaphoneCharacter("S", "TS", primaryKey, alternateKey);
                            }
                            else
                                AddMetaphoneCharacter("S", null, primaryKey, alternateKey);

                        if (word[current + 1] == 'Z')
                            current += 2;
                        else
                            current += 1;
                        break;

                    default:
                        current += 1;
                        break;
                }
            }

            //Finally, chop off the keys at the proscribed length
            string primaryKeyString = primaryKey.ToString(0, Math.Min(primaryKey.Length, METAPHONE_KEY_LENGTH));
            string alternateKeyString = alternateKey.ToString(0, Math.Min(alternateKey.Length, METAPHONE_KEY_LENGTH));

            return new KeyValuePair<string, string>(primaryKeyString, alternateKeyString);
        }

        private struct SimilarityUnit
        {
            public int Position1, Position2;
            public int End1, End2;
            public int Maximum;
        }

        private static SimilarityUnit SimilarStringHelper(string txt1, int len1, string txt2, int len2)
        {
            SimilarityUnit unit = new SimilarityUnit();
            unit.Maximum = 0;

            int j1 = 0, j2 = 0, l = 0;
            for (int i1 = 0; i1 < len1; i1++)
            {
                for (int i2 = 0; i2 < len2; i2++)
                {
                    l = 0; j1 = 0; j2 = 0;
                    while ((i1 + j1 < len1) && (i2 + j2 < len2))
                    {
                        char ch1 = txt1[i1 + j1];
                        j1++;
                        char ch2 = txt2[i2 + j2];
                        j2++;
                        if (ch1 != ch2)
                        {
                            j1--;
                            j2--;
                            break;
                        }
                        l++;
                    }
                    if (l > unit.Maximum)
                    {
                        unit.Maximum = l;
                        unit.Position1 = i1;
                        unit.End1 = j1;
                        unit.Position2 = i2;
                        unit.End2 = j2;
                    }
                }
            }
            return unit;
        }

        private static int SimilarString(string s, int sLen, string t, int tLen)
        {
            SimilarityUnit similar = SimilarStringHelper(s, sLen, t, tLen);
            int sum = similar.Maximum;
            if (sum != 0)
            {
                if (similar.Position1 != 0 && similar.Position2 != 0)
                {
                    sum += SimilarString(s, similar.Position1, t, similar.Position2);
                }
                if ((similar.Position1 + similar.End1 < sLen) && (similar.Position2 + similar.End2 < sLen))
                {
                    sum += SimilarString(s.Substring(similar.Position1 + similar.End1), s.Length - similar.Position1 - similar.End1,
                        t.Substring(similar.Position2 + similar.End2), t.Length - similar.Position2 - similar.End2);
                }
            }
            return sum;
        }

        // ported from PHP source here:
        // http://lxr.php.net/source/php-src/ext/standard/string.c#4536


        public static float SimilarText(this string s, string t)
        {
            if (s == null)
                throw new ArgumentNullException("s");
            if (t == null)
                throw new ArgumentNullException("t");

            int sim = SimilarString(s, s.Length, t, t.Length);
            return sim * 200.0f / (s.Length + t.Length);
        }
    }
}
