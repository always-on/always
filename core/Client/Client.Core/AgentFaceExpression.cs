using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;

namespace Agent.Core
{
    public class AgentFaceExpression
    {
        public static readonly AgentFaceExpression Nod = new AgentFaceExpression("<HEADNOD/>");
        public static readonly AgentFaceExpression Concern = new AgentFaceExpression("<FACE EXPR = \"CONCERN\"/>");
        public static readonly AgentFaceExpression Smile = new AgentFaceExpression("<FACE EXPR = \"SMILE\"/>");
        public static readonly AgentFaceExpression Warm = new AgentFaceExpression("<FACE EXPR = \"WARM\"/>");
       //public static readonly AgentFaceExpression Blink = new AgentFaceExpression(new RagClient.Agent.Actions.Blink());
        public static readonly AgentFaceExpression Eyebrows_Up = new AgentFaceExpression("<EYEBROWS DIR = \"UP\"/>");
        public static readonly AgentFaceExpression Eyebrows_Down = new AgentFaceExpression("<EYEBROWS DIR = \"DOWN\"/>");



        
        private readonly string action;
        private AgentFaceExpression(string action)
        {
            this.action = action;
        }
        public string getAction()
        {
            return action;
        }

        public static AgentFaceExpression valueOf(string name)
        {
            FieldInfo field = typeof(AgentFaceExpression).GetField(name);
            if (field != null)
                return (AgentFaceExpression)field.GetValue(null);
            return Warm;
        }
    }
}
