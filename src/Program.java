package grgCode;

public class Program 
{   
    //code variables
    private String code_rawCode;
    private String[] code_code;
    private int code_numberOfLines;
    
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
    
    public Program(String code)
    {
	code_rawCode = code;
	code_rawCode += "\n";
	code_numberOfLines = 0;
	
	var_ints = new Storage(DECLAREINT);
	var_strings = new Storage(DECLARESTRING);
	var_booleans = new Storage(DECLAREBOOLEAN);
	var_doubles = new Storage(DECLAREDOUBLE);
	
	var_variables[0] = var_ints;
	var_variables[1] = var_strings;
	var_variables[2] = var_booleans;
	var_variables[3] = var_doubles;
	
	var_DATA = new Storage("");
	
	program_preprocess();
    }
    
    private void program_preprocess()
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
	code_code = new String[code_numberOfLines];
	
	//populate array
	int lastIndex = 0;
	int currentLine = 0;
	
	for(int i = 0; i < code_rawCode.length(); i++)
	{
	    if(code_rawCode.charAt(i) == '\n')
	    {
		code_code[currentLine] = code_rawCode.substring(lastIndex, i);
		currentLine++;
		lastIndex = i + 1;
	    }
	}
    }

    public void program_process()
    {
	boolean programFinished = false;
	boolean error = false;
	
	//check for some basic errors (no start, no end)
	if(!condition_startIsPresent())
	{
	    error = true;
	    programFinished = true;
	}
	
	if(!error & !condition_endIsPresent())
	{
	    error = true;
	    programFinished = true;
	}
	
	int currentLine = 1;
	
	while(!programFinished)
	{
	    //check if the line is blank or a comment, if so, skip it
	    if(condition_lineIsBlank(currentLine) || condition_lineIsComment(currentLine))
	    {
		currentLine++;
	    }
	    
	    //check if the program is done
	    if(condition_lineIsEnd(currentLine))
	    {
		programFinished = true;
	    }
	    
	    //GETS
	    while(!programFinished & condition_lineGets(currentLine))
	    {
		String name = "";
		int index = -1;
		int secondSpaceIndex = -1;
	
		for(int i = 0; i < code_code[currentLine].length() - GET.length(); i++)
		{
		    if(code_code[currentLine].substring(i, i + GET.length()).equals(GET))
		    {
			index = i;
			
			for(int j = index + 1; j < code_code[currentLine].length(); j++)
			{
			    if(code_code[currentLine].charAt(j) == ' ')
			    {
				secondSpaceIndex = j;
				j = code_code[currentLine].length();
			    }
			}
			
			//magic
			secondSpaceIndex += 2;
			
			name = code_code[currentLine].substring(i + GET.length() + 1, secondSpaceIndex);
		    }
		}
	
		if(var_DATA.nameIsPresent(name))
		{
		    code_code[currentLine] = code_code[currentLine].substring(0, index);
	    
		    if(var_DATA.get(name).equals(DECLAREINT))
		    {
			code_code[currentLine] += var_ints.get(name);
		    }
	    
		    if(var_DATA.get(name).equals(DECLARESTRING))
		    {
			code_code[currentLine] += var_strings.get(name);
		    }
	    
		    if(var_DATA.get(name).equals(DECLAREBOOLEAN))
		    {
			code_code[currentLine] += var_booleans.get(name);
		    }
	    
		    if(var_DATA.get(name).equals(DECLAREDOUBLE))
		    {
			code_code[currentLine] += var_doubles.get(name);
		    }
		}
		else
		{
		    error_throwError(currentLine, "Could not get variable " + name + " because the variable does not exist");
		    programFinished = true;
		    error = true;
		}
	    }
	    
	    //EVALUATES
	    if(!programFinished & condition_lineEvaluates(currentLine))
	    {
	    	String input = "";
	    	int index = 0;
	    	
	    	for(int i = 0; i < code_code[currentLine].length() - EVALUATEEXPRESSION.length(); i++)
	    	{
	    	    if(code_code[currentLine].substring(i, i + EVALUATEEXPRESSION.length()).equals(EVALUATEEXPRESSION))
	    	    {
	    		input = code_code[currentLine].substring(i + EVALUATEEXPRESSION.length() + 1);
	    		index = i;
	    	    }
	    	}

	    	code_code[currentLine] = code_code[currentLine].substring(0, index);
	    	code_code[currentLine] += new Evaluation(input).getResult();
	    }
	    	    
	    //DECLAREINT
	    if(!programFinished & condition_lineDeclaresInt(currentLine))
	    {
		String name = "";
		String value = "";
	
		for(int i = DECLAREINT.length() + 1; i < code_code[currentLine].length(); i++)
		{
		    if(code_code[currentLine].charAt(i) == ' ')
		    {
			name = code_code[currentLine].substring(DECLAREINT.length() + 1, i);
			value = code_code[currentLine].substring(i + 1);
		    }
		}
	
		if(!var_DATA.nameIsPresent(name))
		{
		    try
		    {
			var_ints.add(name, Integer.parseInt(value));
			var_DATA.add(name, DECLAREINT);
		    }
		    catch (NumberFormatException e)
		    {
			error_throwError(currentLine, value + " is not an int");
			programFinished = true;
			error = true;
		    }
		}
		else
		{
		    error_throwError(currentLine, "Could not initialize int " + name + " because the name is already in use");
		    programFinished = true;
		    error = true;
		}
	    }
	    
	    //DECLARESTRING
	    if(!programFinished & condition_lineDeclaresString(currentLine))
	    {
		String name = "";
		String value = "";
	
		for(int i = DECLARESTRING.length() + 1; i < code_code[currentLine].length(); i++)
		{
		    if(code_code[currentLine].charAt(i) == ' ')
		    {
			name = code_code[currentLine].substring(DECLARESTRING.length() + 1, i);
			value = code_code[currentLine].substring(i + 1);
		    }
		}
	
		if(!var_DATA.nameIsPresent(name))
		{ 
		    var_strings.add(name, value);
		    var_DATA.add(name, DECLARESTRING);
		}
		else
		{
		    error_throwError(currentLine, "Could not initialize string " + name + " because the name is already in use");
		    programFinished = true;
		    error = true;
		}
	    }
	    
	    //DECLAREBOOLEAN
	    if(!programFinished & condition_lineDeclaresBoolean(currentLine))
	    {
		String name = "";
		String value = "";
	
		for(int i = DECLAREBOOLEAN.length() + 1; i < code_code[currentLine].length(); i++)
		{
		    if(code_code[currentLine].charAt(i) == ' ')
		    {
			name = code_code[currentLine].substring(DECLAREBOOLEAN.length() + 1, i);
			value = code_code[currentLine].substring(i + 1);
		    }
		}
	
		if(!var_DATA.nameIsPresent(name))
		{
		    if(value.equals("true") || value.equals("false"))
		    {
			var_booleans.add(name, Boolean.parseBoolean(value));
			var_DATA.add(name, DECLAREBOOLEAN);
		    }
		    else
		    {
			error_throwError(currentLine, value + " is not a boolean");
			programFinished = true;
			error = true;
		    }
		}
		else
		{
		    error_throwError(currentLine, "Could not initialize boolean " + name + " because the name is already in use");
		    programFinished = true;
		    error = true;
		}
	    }
	    
	    //DECLAREDOUBLE
	    if(!programFinished & condition_lineDeclaresDouble(currentLine))
	    {
		String name = "";
		String value = "";
	
		for(int i = DECLAREDOUBLE.length() + 1; i < code_code[currentLine].length(); i++)
		{
		    if(code_code[currentLine].charAt(i) == ' ')
		    {
			name = code_code[currentLine].substring(DECLAREDOUBLE.length() + 1, i);
			value = code_code[currentLine].substring(i + 1);
		    }
		}
	
		if(!var_DATA.nameIsPresent(name))
		{
		    try
		    {
			var_doubles.add(name, Double.parseDouble(value));
			var_DATA.add(name, DECLAREDOUBLE);   
		    }
		    catch (NumberFormatException e)
		    {
			error_throwError(currentLine, value + " is not a double");
			programFinished = true;
			error = true;
		    }
		}
		else
		{
		    error_throwError(currentLine, "Could not initialize double " + name + " because the name is already in use");
		    programFinished = true;
		    error = true;
		}
	    }
	    
	    //DELETES
	    if(!programFinished & condition_lineDeletes(currentLine))
	    {
		String name = code_code[currentLine].substring(DELETE.length() + 1);
	
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
		}
		else
		{
		    error_throwError(currentLine, "Could not delete variable " + name + " because the variable does not exist");
		    programFinished = true;
		    error = true;
		}
	    }
	      
	    //SETS
	    if(!programFinished & condition_lineSets(currentLine))
	    {
		String name = "";
		String value = "";
	
		for(int i = SET.length() + 1; i < code_code[currentLine].length(); i++)
		{
		    if(code_code[currentLine].charAt(i) == ' ')
		    {
			name = code_code[currentLine].substring(SET.length() + 1, i);
			value = code_code[currentLine].substring(i + 1);
		    }
		}
	
		if(var_DATA.nameIsPresent(name))
		{
		    if(var_DATA.get(name).equals(DECLAREINT))
		    {
			try
			{
			    var_ints.set(name, Integer.parseInt(value));
			}
			catch (NumberFormatException e)
			{
			    error_throwError(currentLine, value + " is not an int");
			    programFinished = true;
			    error = true;
			}
		    }
		    
		    if(var_DATA.get(name).equals(DECLARESTRING))
		    {
			var_strings.set(name, value);
		    }
		    
		    if(var_DATA.get(name).equals(DECLAREBOOLEAN))
		    {
			if(value.equals("true") || value.equals("false"))
			{
			    var_booleans.set(name, Boolean.parseBoolean(value));
			}
			else
			{
			    error_throwError(currentLine, value + " is not a boolean");
			    programFinished = true;
			    error = true;
			}
		    }

		    if(var_DATA.get(name).equals(DECLAREDOUBLE))
		    {	
			try
			{
		    	    var_doubles.set(name, Double.parseDouble(value));
		    	}
			catch (NumberFormatException e)
		    	{
			    error_throwError(currentLine, value + " is not an double");
			    programFinished = true;
			    error = true;
		    	}
		    }
		}
		else
		{
		    error_throwError(currentLine, "Could not set variable " + name + " because the variable does not exist");
		    programFinished = true;
		    error = true;
		}
	    }
	    
	    //PRINTS
	    if(!programFinished & condition_linePrints(currentLine))
	    {
		String value = code_code[currentLine].substring(PRINT.length() + 1);
		GUIHandler.update_output(value);
	    }
	    
	    if(!programFinished & condition_linePrintsNewLine(currentLine))
	    {
		GUIHandler.update_output("\n");
	    }
	    
	    //IF
	    if(!programFinished & condition_lineStartsIf(currentLine))
	    {
		String value1 = "";
		String value2 = "";
		
		for(int i = STARTIF.length() + 1; i < code_code[currentLine].length(); i++)
		{
		    if(code_code[currentLine].charAt(i) == ' ')
		    {
			value1 = code_code[currentLine].substring(STARTIF.length() + 1, i);
			value2 = code_code[currentLine].substring(i + 1);
		    }
		}
		
		if(value1.equals(value2))
		{
		    //do nothing, continue the code as normal
		}
		else
		{
		    for(int i = currentLine; i < code_numberOfLines; i++)
		    {
			if(condition_lineEndsIf(i))
			{
			    currentLine = i;
			    i = code_numberOfLines;
			}
			
			if(i == code_numberOfLines - 1)
			{
			    error_throwError(currentLine, "Could not find endIf statement");
			    programFinished = true;
			    error = true;
			}
		    }
		}
	    }
	    
	    GUIHandler.update_variables_clear();
	    
	    for(int i = 0; i < var_variables.length; i++)
	    {
		GUIHandler.update_variables(var_variables[i].displayVars());
	    }
	    
	    currentLine++;
	}
	
	if(!error)
	{
	    GUIHandler.update_output("Program Finished!\n");
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
    
    private boolean condition_lineIsBlank(int line)
    {
	if(code_code[line].equals(""))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    private boolean condition_lineIsComment(int line)
    {
	if(code_code[line].startsWith("//"))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    private boolean condition_lineIsEnd(int line)
    {
	if(code_code[line].equals(ENDCOMMAND))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    private boolean condition_lineDeclaresInt(int line)
    {	
	if(code_code[line].startsWith(DECLAREINT))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    private boolean condition_lineDeclaresString(int line)
    {
	if(code_code[line].startsWith(DECLARESTRING))
	{
	    return true;
	}
	else
	{
	    return false;
	}
    }
    
    private boolean condition_lineDeclaresBoolean(int line)
    {
    	if(code_code[line].startsWith(DECLAREBOOLEAN))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }

    private boolean condition_lineDeclaresDouble(int line)
    {
	if(code_code[line].startsWith(DECLAREDOUBLE))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_lineDeletes(int line)
    {
	if(code_code[line].startsWith(DELETE))
    	{
    	    return true;
    	}
	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_lineSets(int line)
    {
	if(code_code[line].startsWith(SET))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_lineGets(int line)
    {
	if(code_code[line].contains(GET))
    	{
    	    return true;
    	}
	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_linePrints(int line)
    {
	if(code_code[line].startsWith(PRINT + " "))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_linePrintsNewLine(int line)
    {
	if(code_code[line].startsWith(PRINTNEWLINE))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_lineEvaluates(int line)
    {
    	if(code_code[line].contains(EVALUATEEXPRESSION))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_lineStartsIf(int line)
    {
	if(code_code[line].startsWith(STARTIF))
    	{
    	    return true;
    	}
    	else
    	{
    	    return false;
    	}
    }
    
    private boolean condition_lineEndsIf(int line)
    {
	if(code_code[line].equals(ENDIF))
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
