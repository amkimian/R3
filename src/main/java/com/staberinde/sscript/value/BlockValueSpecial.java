package com.staberinde.sscript.value;

import java.util.Objects;

public class BlockValueSpecial extends AbstractBlockValue<SpecialValue> {

        private static final ValueType TYPE = ValueType.SPECIAL;

        public static final BlockValueSpecial BREAK = new BlockValueSpecial(SpecialValue.BREAK);
        public static final BlockValueSpecial CONTINUE = new BlockValueSpecial(SpecialValue.CONTINUE);
        public static final BlockValueSpecial RETURN = new BlockValueSpecial(SpecialValue.RETURN);

        private final SpecialValue value;

        public static BlockValueSpecial from(final SpecialValue o) {
            return new BlockValueSpecial(o);
        }

        private BlockValueSpecial(final SpecialValue o) {
            this.value = o;
        }

        @Override
        public ValueType getType() {
            return TYPE;
        }

        @Override
        protected SpecialValue getValue() {
            return value;
        }

        @Override
        public SpecialValue asSpecial() {
            return this.getValue();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BlockValueSpecial)) {
                return false;
            }
            return this.value.equals(((BlockValueSpecial)obj).getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value, TYPE);
        }
}
