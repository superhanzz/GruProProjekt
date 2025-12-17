package CapableSimulator.Utils;

public class CapableEnums {

    public enum DayNightStatus {
        DAY,
        NIGHT,
        DUSK,
        DAWN,
        MIDNIGHT;
    }


    public enum AnimalSize {
        BABY("Small"),
        ADULT("Big");

        public final String label;

        private AnimalSize(String label) {
            this.label = label;
        }
    }

    public enum FungiState {
        FUNGI("Fungi"),
        NORMAL("Normal");

        public final String label;

        private FungiState(String label) {
            this.label = label;
        }
    }

    public enum AnimalState {
        AWAKE("Awake"),
        SLEEPING("Sleeping"),;

        public final String label;

        private AnimalState(String label) {
            this.label = label;
        }
    }


    public enum WolfType {
        ALPHA("Alpha"),
        NPC("Npc");

        public final String label;

        private WolfType(String label) {
            this.label = label;
        }
    }


    public enum FungiType {
        FUNGUS,
        CORDYCEP;
    }

    public enum SimulationType {
        NORMAL,
        TEST;
    }
}
