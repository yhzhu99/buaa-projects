import java.util.ArrayList;
import java.util.List;

public class OutPutBinary {
    Table table;
    List<Byte> output;

    int magic=0x72303b3e;
    int version=0x00000001;

    public OutPutBinary(Table table) {
        this.table=table;
        output = new ArrayList<>();
    }

    public List<Byte> generate() {
        //magic
        List<Byte> magic=int2bytes(4,this.magic);
        output.addAll(magic);
        //version
        List<Byte> version=int2bytes(4,this.version);
        output.addAll(version);

        List<Token> globals=table.getGlobal();
        //globals.count
        List<Byte> global_count=int2bytes(4,globals.size());
        output.addAll(global_count);

        for(int i=0;i<globals.size();i++){
            Token global=globals.get(i);
            List<Byte> global_is_const=int2bytes(1,global.getIs_const());
            output.addAll(global_is_const);

            List<Byte> global_value=getValueByte(global);

            List<Byte> global_value_count=int2bytes(4,global_value.size());
            output.addAll(global_value_count);

            output.addAll(global_value);
        }

        List<FunctionTable> functionTables=table.getFunctionTables();
        //functions.count
        List<Byte> functions_count=int2bytes(4,functionTables.size());
        output.addAll(functions_count);

        for(int i=0;i<functionTables.size();i++){
            FunctionTable functionTable=functionTables.get(i);
            //name
            List<Byte> name=int2bytes(4,functionTable.getPos());
            output.addAll(name);

            //ret_slots
            List<Byte> ret_slots=int2bytes(4,functionTable.getReturnSoltNmum());
            output.addAll(ret_slots);

            //params_slots;
            List<Byte> params_slots=int2bytes(4,functionTable.getParamSoltNum());
            output.addAll(params_slots);

            //loc_slots;
            List<Byte> loc_slots=int2bytes(4,functionTable.getVarSoltNmum());
            output.addAll(loc_slots);

            List<Instruction> instructions=functionTable.getInstructions();
            //body_count
            List<Byte> body_count=int2bytes(4,instructions.size());
            output.addAll(body_count);

            //instructions
            for(Instruction instruction:instructions){
                //type
                List<Byte> type=int2bytes(1,instruction.getType());
                output.addAll(type);
                if(instruction.getLength()==1){
                    List<Byte>  x;
                    if(instruction.getType()==1)
                        x=long2bytes(8,instruction.getX());
                    else x=long2bytes(4,instruction.getX());
                    output.addAll(x);
                }
            }
        }
        return output;
    }

    private List<Byte> getValueByte(Token global) {
        List<Byte> bytes=new ArrayList<>();
        if(global.getNameType()== NameType.Proc||global.getNameType()==NameType.String)
            bytes=String2bytes(global.getValueString());
        else if(global.getNameType()==NameType.Char)
            bytes= Char2bytes((char)global.getValue());
        else {
            if(global.getTy()== TokenType.INT_KW)
                bytes= long2bytes(8,0);
        }
        return bytes;
    }

    private List<Byte> Char2bytes(char value) {
        List<Byte>  AB=new ArrayList<>();
        AB.add((byte)(value&0xff));
        return AB;
    }

    private List<Byte> String2bytes(String valueString) {
        List<Byte>  AB=new ArrayList<>();
        for (int i=0;i<valueString.length();i++){
            char ch=valueString.charAt(i);
            if (ch!='\\')
                AB.add((byte)(ch&0xff));
            else {
                i++;
                ch=valueString.charAt(i);
                if (ch=='\\')
                    AB.add((byte)('\\'&0xff));
                else if (ch=='\"')
                    AB.add((byte)('\"'&0xff));
                else if (ch=='\'')
                    AB.add((byte)('\''&0xff));
                else if (ch=='n')
                    AB.add((byte)('\n'&0xff));
                else if (ch=='r')
                    AB.add((byte)('\r'&0xff));
                else if (ch=='t')
                    AB.add((byte)('\t'&0xff));
            }
        }
        return AB;
    }

    private List<Byte> long2bytes(int length, long target) {
        ArrayList<Byte> bytes = new ArrayList<>();
        int start = 8*(length-1);
        for(int i = 0 ; i < length; i++){
            bytes.add((byte) (( target >> ( start - i * 8 )) & 0xFF ));
        }
        return bytes;
    }

    private ArrayList<Byte> int2bytes(int length,int target){
        ArrayList<Byte> bytes = new ArrayList<>();
        int start = 8*(length-1);
        for(int i = 0 ; i < length; i++){
            bytes.add((byte) (( target >> ( start - i * 8 )) & 0xFF ));
        }
        return bytes;
    }

}
