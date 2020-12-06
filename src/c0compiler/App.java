package c0compiler;

import c0compiler.analyser.Analyser;
import c0compiler.analyser.FunctionTable;
import c0compiler.analyser.Table;
import c0compiler.tokenizer.StringIter;
import c0compiler.tokenizer.Token;
import c0compiler.tokenizer.TokenType;
import c0compiler.tokenizer.Tokenizer;
import c0compiler.util.OutPutBinary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        InputStream input;
        PrintStream output;
        DataOutputStream out;
        if(args.length<3)
            throw new Exception("wrong num");
        String inputFileName=args[1],outputFileName=args[2];
        input = new FileInputStream(new File(inputFileName));
        output = new PrintStream(new FileOutputStream(new File(outputFileName)));
        out=new DataOutputStream(new FileOutputStream(new File(outputFileName)));
        Scanner scanner;
        scanner = new Scanner(input);
        StringIter iter = new StringIter(scanner);
        Tokenizer tokenizer = tokenize(iter);
        String s=args[0];
        // tokenize
        switch (s) {
            case "t" -> {
                List<Token> tokens = new ArrayList<Token>();
                try {
                    while (true) {
                        Token token = tokenizer.nextToken();
                        if (token.getTokenType().equals(TokenType.EOF)) {
                            break;
                        }
                        tokens.add(token);
                    }
                } catch (Exception e) {
                    // 遇到错误不输出，直接退出
                    System.err.println(e);
                    System.exit(0);
                    return;
                }
                for (Token token : tokens) {
                    output.println(token.toString());
                }
            }
            case "l" -> {
                // analyze
                Analyser analyzer = new Analyser(tokenizer);
                Table table;
                List<FunctionTable> functionTables;
                List<Token> global;
                table = analyzer.analyse();
                table.generate();

                global = table.getGlobal();
                functionTables = table.getFunctionTables();
                for (Token token : global) {
                    output.println(token);
                }
                for (FunctionTable function : functionTables) {
                    output.println(function.getName() + " pos:" + function.getPos() + " params:" + function.getParamSoltNum() + " var:" + function.getVarSoltNmum() + " -> " + function.getReturnSoltNmum());
                    output.println(function.getInstructions());
                }
            }
            case "tb" -> {
                // analyze
                Analyser analyzer = new Analyser(tokenizer);
                Table table;
                List<FunctionTable> functionTables;
                List<Token> global;
                table = analyzer.analyse();
                table.generate();

                OutPutBinary outPutBinary = new OutPutBinary(table);
                List<Byte> bs = outPutBinary.generate();
                output.println(bs);
            }
            case "b" -> {
                // analyze
                Analyser analyzer = new Analyser(tokenizer);
                Table table;
                List<FunctionTable> functionTables;
                List<Token> global;
                table = analyzer.analyse();
                table.generate();

                OutPutBinary outPutBinary = new OutPutBinary(table);
                List<Byte> bs = outPutBinary.generate();
                byte[] temp = new byte[bs.size()];
                for (int i = 0; i < bs.size(); i++)
                    temp[i] = bs.get(i);
                out.write(temp);
            }
            default -> throw new Exception("noe writ");
        }
    }



    private static Tokenizer tokenize(StringIter iter) {
        return new Tokenizer(iter);
    }
}
