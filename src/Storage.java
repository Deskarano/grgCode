package grgCode;

public class Storage
{
    private ExpandingArray names;
    private ExpandingArray values;

    private int length;
    private String type;

    public Storage(String dataType)
    {
        type = dataType;

        names = new ExpandingArray();
        values = new ExpandingArray();
        length = 0;
    }

    public void add(String newName, Object newValue)
    {
        names.add(newName);
        values.add(newValue);

        length++;
    }

    public void delete(String name)
    {
        values.deleteIndex(names.getIndex(name));
        names.deleteObject(name);

        length--;
    }

    public void set(String name, Object value)
    {
        int index = 0;

        for (int i = 0; i < length; i++)
        {
            if (names.get(i).equals(name))
            {
                index = i;
            }
        }

        values.set(index, value);
    }

    public Object get(String name)
    {
        int index = 0;

        for (int i = 0; i < length; i++)
        {
            if (names.get(i).equals(name))
            {
                index = i;
            }
        }

        return values.get(index);
    }

    public boolean nameIsPresent(String name)
    {
        return names.isPresent(name);
    }

    public String displayVars()
    {
        String returnString = "";

        for (int i = 0; i < length; i++)
        {
            returnString += names.get(i);
            returnString += " (";
            returnString += type;
            returnString += "): \t";
            returnString += values.get(i);
            returnString += "\n";
        }

        return returnString;
    }
}
