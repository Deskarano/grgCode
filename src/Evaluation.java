package grgCode;

public class Evaluation
{
    private String evalString ;
    private int numberOfOperations;
    
    private double finalResultDouble;

    public Evaluation(String input)
    {
        evalString = input;
        numberOfOperations = 0;
    }
    
    public Object getResult()
    {
	process();
	
	if((int)finalResultDouble == finalResultDouble)
	{
	    return (int)finalResultDouble;
	}
	else
	{
	    return finalResultDouble;
	}
    }

    
    private void process()
    {
        //count the number of operations
        for(int i = 0; i < evalString.length(); i++)
        {
            char currentChar = evalString.charAt(i);
            if(currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/')
            {
                numberOfOperations++;
            }
        }
        
        while(numberOfOperations > 0)
        {
            //find the first operation according to PEMDAS
            int indexOfOperation = 0;
            char operation = ' ';
            String num1 = "";
            String num2 = "";
            int num1Index = 0;
            int num2Index = 0;
            double num1Double, num2Double;
            double resultOfOperation = 0;
            String resultString = "";
            
            //multiply and divide
            for(int i = 0; i < evalString.length(); i++)
            {
                char currentChar = evalString.charAt(i);
                if(currentChar == '*' || currentChar == '/')
                {
                    indexOfOperation = i;
                    operation = currentChar;
                    i = evalString.length();
                }
            }
            
            //add and subtract
            if(operation == ' ')
            {
                for(int i = 0; i < evalString.length(); i++)
                {
                    char currentChar = evalString.charAt(i);
                    if(currentChar == '+' || currentChar == '-')
                    {
                        indexOfOperation = i;
                        operation = currentChar;
                        i = evalString.length();
                    }
                }
            }
            
            //find the two numbers
            for(int i = indexOfOperation - 2; i >= 0; i--)
            {
                if(i == 0)
                {
                    num1 = evalString.substring(0, indexOfOperation - 1);
                    num1Index = i;
                    i = -1;
                }
                else if(evalString.charAt(i) == ' ')
                {
                    num1 = evalString.substring(i + 1, indexOfOperation - 1);
                    num1Index = i + 1;
                    i = -1;
                }
            }
            
            for(int i = indexOfOperation + 2; i < evalString.length(); i++)
            {
                if(i == evalString.length() - 1)
                {
                    num2 = evalString.substring(indexOfOperation + 2);
                    num2Index = i + 1;
                    i = evalString.length();
                }
                else if(evalString.charAt(i) == ' ')
                {
                    num2 = evalString.substring(indexOfOperation + 2, i + 1);
                    num2Index = i + 1;
                    i = evalString.length();
                }
            }
            
            num1Double = Double.parseDouble(num1);
            num2Double = Double.parseDouble(num2);
            
            if(operation == '+')
            {
                resultOfOperation = num1Double + num2Double;
            }
            if(operation == '-')
            {
                resultOfOperation = num1Double - num2Double;
            }
            if(operation == '*')
            {
                resultOfOperation = num1Double * num2Double;
            }
            if(operation == '/')
            {
                resultOfOperation = num1Double / num2Double;
            }    
            
            resultString += evalString.substring(0, num1Index);
            resultString += resultOfOperation;
            resultString += evalString.substring(num2Index - 1);
            
            evalString = resultString;
            
            numberOfOperations--;
            
            if(numberOfOperations == 0)
            {
                finalResultDouble = resultOfOperation;
            }
        }    
    }
}
