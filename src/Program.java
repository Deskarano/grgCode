package grgCode;

public class Program 
{   
    //code variables
    private String code_rawCode;
    private String[] code_code;
    private int code_numberOfLines;
    private int code_currentLineNum;
    private String code_currentLine;
    
    //variable variables
    private Storage var_ints;
    private Storage var_strings;
    private Storage var_booleans;
    private Storage var_doubles;
    
    private Storage var_DATA;
    
    private Storage[] var_variables = new Storage[4];
    
    //command variables
    public static String STARTCOMMAND = "start";
    public static String ENDCOMMAND = "end";
    
    public static String DECLAREINT = "int";
    public static String DECLARESTRING = "string";
    public static String DECLAREBOOLEAN = "boolean";
    public static String DECLAREDOUBLE = "double";
    
    public static String SET = "set";
    public static String GET = "get";
    public static String DELETE = "delete";
    public static String PRINT = "print";
    public static String PRINTNEWLINE = "printNL";
    public static String EVALUATEEXPRESSION = "eval";
    public static String STARTIF = "if";
    public static String ENDIF = "endIf";
    public static String STARTWHILE = "while";
    public static String ENDWHILE = "endWhile";
    
    public Program(String code)
    {
        code_rawCode = code;
        code_rawCode += "\n";
        code_numberOfLines = 0;
        code_currentLineNum = 0;
             
        var_ints = new Storage(DECLAREINT);
        var_strings = new Storage(DECLARESTRING);
        var_booleans = new Storage(DECLAREBOOLEAN);
        var_doubles = new Storage(DECLAREDOUBLE);
        
        var_variables[0] = var_ints;
        var_variables[1] = var_strings;
        var_variables[2] = var_booleans;
        var_variables[3] = var_doubles;
        
        var_DATA = new Storage("");
    }
    
    public void program_preprocess()
    {
        //count number of newline characters in the program
        for(int i = 0; i < code_rawCode.length(); i++)
        {
            if(code_rawCode.charAt(i) == '\n')
            {
                code_numberOfLines++;
            }      
        }   
        
        //allocate array, one index for each line of code
        String[] temp = new String[code_numberOfLines];
        
        //populate array
        int lastStringIndex = 0;
        int currentCodeLine = 0;
        
        for(int i = 0; i < code_rawCode.length(); i++)
        {
            if(code_rawCode.charAt(i) == '\n')
            {
                temp[currentCodeLine] = code_rawCode.substring(lastStringIndex, i);
                currentCodeLine++;
                lastStringIndex = i + 1;
            }
        }
        
        code_numberOfLines = 0;
        
        //count number of non-blank, non-comment lines
        for(int i = 0; i < temp.length; i++)
        {
            if(!temp[i].equals(""))
            {
        	code_numberOfLines++;
            }
        }
        
        code_code = new String[code_numberOfLines];
        int lastCodeIndex = 0;
        
        for(int i = 0; i < temp.length; i++)
        {
            if(!temp[i].equals(""))
            {
        	code_code[lastCodeIndex] = temp[i];
        	lastCodeIndex++;
            }
        }                
    }
    
    public void program_process()
    {
	if(condition_startIsPresent() && condition_endIsPresent()) 
        {
	    while(code_currentLineNum < code_numberOfLines)
	    {
		if(!program_processLine())
		{
		    code_currentLineNum = code_numberOfLines;
		}
	    }
	    
	    //System.out.println("----------------------------");
	}
    }

    public boolean program_processLine()
    {
	code_currentLine = code_code[code_currentLineNum];
	//System.out.println("Processing " + code_currentLine);
	
	//start
	if(condition_lineIsStart())
	{
	    code_currentLineNum++;
	    return true;
	}
	
	//end
	if(condition_lineIsEnd())
	{
	    code_currentLineNum++;
	    return true;
	}
	
        //GETS
        while(condition_lineGets())
        {
            String name = "";
            int index = -1;
            int secondSpaceIndex = -1;
    
            index = code_currentLine.indexOf(GET);
            secondSpaceIndex = code_currentLine.indexOf(' ', index + GET.length() + 1);        

            if(secondSpaceIndex == -1)
    	    {
        	name = code_currentLine.substring(index + GET.length() + 1);
    	    }
    	    else
    	    {
    		name = code_currentLine.substring(index + GET.length() + 1, secondSpaceIndex);
    	    }
    
            if(var_DATA.nameIsPresent(name))
            {
        	String firstHalf = code_currentLine.substring(0, index);
        	String secondHalf = "";
            
        	if(!(secondSpaceIndex == -1))
        	{
        	    secondHalf = code_currentLine.substring(secondSpaceIndex);
        	}
            
        	code_currentLine = firstHalf;
        
        	if(var_DATA.get(name).equals(DECLAREINT))
        	{
        	    code_currentLine += var_ints.get(name);
        	}
        
        	if(var_DATA.get(name).equals(DECLARESTRING))
        	{
        	    code_currentLine += var_strings.get(name);
        	}
        
        	if(var_DATA.get(name).equals(DECLAREBOOLEAN))
        	{
        	    code_currentLine += var_booleans.get(name);
        	}
        
        	if(var_DATA.get(name).equals(DECLAREDOUBLE))
        	{
        	    code_currentLine += var_doubles.get(name);
        	}
            
        	code_currentLine += secondHalf;
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not get variable " + name + " because the variable does not exist");
        	return false;
            }
        }
        
        //EVALUATES
        if(condition_lineEvaluates())
        {
            String input = "";
            int index = 0;
        
            index = code_currentLine.indexOf(EVALUATEEXPRESSION);
            input = code_currentLine.substring(index + EVALUATEEXPRESSION.length() + 1);       

            code_currentLine = code_currentLine.substring(0, index);
            code_currentLine += new MathEvaluation(input).getResult();
        }
            
        //DECLAREINT
        if(condition_lineDeclaresInt())
        {
            String name = "";
            String value = "";
    
            for(int i = DECLAREINT.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    name = code_currentLine.substring(DECLAREINT.length() + 1, i);
        	    value = code_currentLine.substring(i + 1);
        	}
            }
    
            if(!var_DATA.nameIsPresent(name))
            {
        	try
        	{
        	    var_ints.add(name, Integer.parseInt(value));
        	    var_DATA.add(name, DECLAREINT);

        	    program_updateVariableList();
        	    code_currentLineNum++;
        	    return true;
        	}
        	catch (NumberFormatException e)
        	{
        	    error_throwError(code_currentLineNum, value + " is not an int");
        	    return false;
        	}
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not initialize int " + name + " because the name is already in use");
        	return false;
            }
        }
        
        //DECLARESTRING
        else if(condition_lineDeclaresString())
        {
            String name = "";
            String value = "";
    
            for(int i = DECLARESTRING.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    name = code_currentLine.substring(DECLARESTRING.length() + 1, i);
        	    value = code_currentLine.substring(i + 1);
        	}
            }
    
            if(!var_DATA.nameIsPresent(name))
            { 
        	var_strings.add(name, value);
        	var_DATA.add(name, DECLARESTRING);
        	
        	program_updateVariableList();
        	code_currentLineNum++;
        	return true;
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not initialize string " + name + " because the name is already in use");
        	return false;
            }
        }
        
        //DECLAREBOOLEAN
        else if(condition_lineDeclaresBoolean())
        {
            String name = "";
            String value = "";
    
            for(int i = DECLAREBOOLEAN.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    name = code_currentLine.substring(DECLAREBOOLEAN.length() + 1, i);
        	    value = code_currentLine.substring(i + 1);
        	}
            }
    
            if(!var_DATA.nameIsPresent(name))
            {
        	if(value.equals("true") || value.equals("false"))
        	{
        	    var_booleans.add(name, Boolean.parseBoolean(value));
        	    var_DATA.add(name, DECLAREBOOLEAN);
        	    
        	    program_updateVariableList();
        	    code_currentLineNum++;
        	    return true;
        	}
        	else
        	{
        	    error_throwError(code_currentLineNum, value + " is not a boolean");
        	    return false;
        	}
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not initialize boolean " + name + " because the name is already in use");
        	return false;
            }
        }
        
        //DECLAREDOUBLE
        else if(condition_lineDeclaresDouble())
        {
            String name = "";
            String value = "";
    
            for(int i = DECLAREDOUBLE.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    name = code_currentLine.substring(DECLAREDOUBLE.length() + 1, i);
        	    value = code_currentLine.substring(i + 1);
        	}
            }
    
            if(!var_DATA.nameIsPresent(name))
            {
        	try
        	{
        	    var_doubles.add(name, Double.parseDouble(value));
        	    var_DATA.add(name, DECLAREDOUBLE);  
        	    
        	    program_updateVariableList();
        	    code_currentLineNum++;
        	    return true;
        	}
        	catch (NumberFormatException e)
        	{
        	    error_throwError(code_currentLineNum, value + " is not a double");
        	    return false;
        	}
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not initialize double " + name + " because the name is already in use");
        	return false;
            }
        }
        
        //DELETES
        else if(condition_lineDeletes())
        {
            String name = code_currentLine.substring(DELETE.length() + 1);
    
            if(var_DATA.nameIsPresent(name))
            {
        	if(var_DATA.get(name).equals(DECLAREINT))
        	{
        	    var_ints.delete(name);
        	}
            
        	if(var_DATA.get(name).equals(DECLARESTRING))
        	{
        	    var_strings.delete(name);
        	}
        
        	if(var_DATA.get(name).equals(DECLAREBOOLEAN))
        	{
        	    var_booleans.delete(name);
        	}
        
        	if(var_DATA.get(name).equals(DECLAREDOUBLE))
        	{
        	    var_doubles.delete(name);
        	}
        
        	var_DATA.delete(name);
        	
        	program_updateVariableList();
        	code_currentLineNum++;
        	return true;
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not delete variable " + name + " because the variable does not exist");
        	return false;
            }
        }
          
        //SETS
        else if(condition_lineSets())
        {
            String name = "";
            String value = "";
    
            for(int i = SET.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    name = code_currentLine.substring(SET.length() + 1, i);
        	    value = code_currentLine.substring(i + 1);
        	}
            }
    
            if(var_DATA.nameIsPresent(name))
            {
        	if(var_DATA.get(name).equals(DECLAREINT))
        	{
        	    try
        	    {
        		var_ints.set(name, Integer.parseInt(value));
        		
        		program_updateVariableList();
        		code_currentLineNum++;
        		return true;
        	    }
        	    catch (NumberFormatException e)
        	    {
        		error_throwError(code_currentLineNum, value + " is not an int");
        		return false;
        	    }
        	}
            
        	if(var_DATA.get(name).equals(DECLARESTRING))
        	{
        	    var_strings.set(name, value);
        	    
        	    program_updateVariableList();
        	    code_currentLineNum++;
        	    return true;
        	}
            
        	if(var_DATA.get(name).equals(DECLAREBOOLEAN))
        	{
        	    if(value.equals("true") || value.equals("false"))
        	    {
        		var_booleans.set(name, Boolean.parseBoolean(value));
        		
        		program_updateVariableList();
        		code_currentLineNum++;
        		return true;
        	    }
        	    else
        	    {
        		error_throwError(code_currentLineNum, value + " is not a boolean");
        		return false;
        	    }
        	}

        	if(var_DATA.get(name).equals(DECLAREDOUBLE))
        	{    
        	    try
        	    {
        		var_doubles.set(name, Double.parseDouble(value));
        		
        		program_updateVariableList();
        		code_currentLineNum++;
        		return true;
        	    }
        	    catch(NumberFormatException e)
        	    {
        		error_throwError(code_currentLineNum, value + " is not an double");
        		return false;
        	    }
        	}
            }
            else
            {
        	error_throwError(code_currentLineNum, "Could not set variable " + name + " because the variable does not exist");
        	return false;
            }
        }
        
        //PRINTS
        else if(condition_linePrints())
        {
            String value = code_currentLine.substring(PRINT.length() + 1);
            GUIHandler.update_output(value);
            
            code_currentLineNum++;
            return true;
        }
        
        else if(condition_linePrintsNewLine())
        {
            GUIHandler.update_output("\n");
            
            code_currentLineNum++;
            return true;
        }
        
        //IF
        else if(condition_lineStartsIf())
        {
            String value1 = "";
            String value2 = "";
        
            for(int i = STARTIF.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    value1 = code_currentLine.substring(STARTIF.length() + 1, i);
        	    value2 = code_currentLine.substring(i + 1);
        	}
            }     
            
            if(!value1.equals(value2))
            {
                for(int i = code_currentLineNum; i < code_numberOfLines; i++)
                {
                    if(code_code[i].equals(ENDIF))
                    {
                        code_currentLineNum = i;
                        return true;
                    }
                    
                    if(i == code_numberOfLines - 1)
                    {
                        error_throwError(code_currentLineNum, "Could not find endIf statement");
                        return false;
                    }
                }
            }
            else
            {
        	code_currentLineNum++;
        	return true;
            }
        }
        
        //TODO: optimize?
        else if(condition_lineEndsIf())
        {
            code_currentLineNum++;
            return true;
        }
        
        //while
        else if(condition_lineStartsWhile())
        {
            String value1 = "";
            String value2 = "";
        
            for(int i = STARTWHILE.length() + 1; i < code_currentLine.length(); i++)
            {
        	if(code_currentLine.charAt(i) == ' ')
        	{
        	    value1 = code_currentLine.substring(STARTWHILE.length() + 1, i);
        	    value2 = code_currentLine.substring(i + 1);
        	}
            }     
            
            if(!value1.equals(value2))
            {
        	for(int i = code_currentLineNum; i < code_numberOfLines; i++)
                {
                    if(code_code[i].equals(ENDWHILE))
                    {
                        code_currentLineNum = i + 1;
                        return true;
                    }
                    
                    if(i == code_numberOfLines - 1)
                    {
                        error_throwError(code_currentLineNum, "Could not find endWhile statement");
                        return false;
                    }
                }
            }
            else
            {
        	code_currentLineNum++;
        	return true;
            }
        }
        
        else if(condition_lineEndsWhile())
        {
            for(int i = code_currentLineNum; i > 0; i--)
            {
        	if(code_code[i].startsWith(STARTWHILE))
        	{
        	    code_currentLineNum = i;
        	}
            }
            return true;
        }
        
        else
        {
            error_throwError(code_currentLineNum, "Could not process " + code_currentLine);
            return false;
        }
          
	return true;	
    }

    private void program_updateVariableList()
    {
	GUIHandler.update_variables_clear();
        
        for(int i = 0; i < var_variables.length; i++)
        {
            GUIHandler.update_variables(var_variables[i].displayVars());
        }
    }
    
    private boolean condition_startIsPresent()
    {
        if(code_code[0].equals(STARTCOMMAND))
        {
            return true;
        }
        else
        {
            error_throwError(0, "Could not find 'start' statement");
            return false;
        }
    }
    
    private boolean condition_endIsPresent()
    {
        if(code_code[code_code.length - 1].equals(ENDCOMMAND))
        {
            return true;
        }
        else
        {
            error_throwError(code_code.length, "Could not find 'end' statement");
            return false;
        }
    }
    
    private boolean condition_lineIsStart()
    {
	if(code_currentLine.equals(STARTCOMMAND))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineIsEnd()
    {
	if(code_currentLine.equals(ENDCOMMAND))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineDeclaresInt()
    {        
        if(code_currentLine.startsWith(DECLAREINT))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineDeclaresString()
    {
        if(code_currentLine.startsWith(DECLARESTRING))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineDeclaresBoolean()
    {
        if(code_currentLine.startsWith(DECLAREBOOLEAN))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean condition_lineDeclaresDouble()
    {
        if(code_currentLine.startsWith(DECLAREDOUBLE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineDeletes()
    {
        if(code_currentLine.startsWith(DELETE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineSets()
    {
        if(code_currentLine.startsWith(SET))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineGets()
    {
        if(code_currentLine.contains(GET))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_linePrints()
    {
        if(code_currentLine.startsWith(PRINT + " "))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_linePrintsNewLine()
    {
        if(code_currentLine.startsWith(PRINTNEWLINE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineEvaluates()
    {
        if(code_currentLine.contains(EVALUATEEXPRESSION))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineStartsIf()
    {
        if(code_currentLine.startsWith(STARTIF))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineEndsIf()
    {
        if(code_currentLine.equals(ENDIF))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineStartsWhile()
    {
        if(code_currentLine.startsWith(STARTWHILE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private boolean condition_lineEndsWhile()
    {
        if(code_currentLine.equals(ENDWHILE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private void error_throwError(int line, String message)
    {
        String errorString = "--- Error (Line ";
        errorString += (line + 1);
        errorString += "): ";
        errorString += message;
        errorString += " ---\n";
    
        GUIHandler.update_output(errorString);
    }
}
