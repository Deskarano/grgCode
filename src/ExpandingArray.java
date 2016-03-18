package grgCode;

public class ExpandingArray
{
    private Object[] data;
    public int length;

    public ExpandingArray()
    {
        data = new Object[0];
        length = 0;
    }

    public void add(Object newObject)
    {
        expand();

        data[length - 1] = newObject;
    }

    public void deleteIndex(int index)
    {
        Object[] tempData = new Object[length - 1];

        int arrayIndex = 0;

        for (int i = 0; i < index; i++)
        {
            tempData[arrayIndex] = data[i];
            arrayIndex++;
        }

        for (int i = index + 1; i < length; i++)
        {
            tempData[arrayIndex] = data[i];
            arrayIndex++;
        }

        length--;
        data = tempData;
    }

    public void deleteObject(Object obj)
    {
        int index = getIndex(obj);

        if (index != -1)
        {
            deleteIndex(index);
        }
    }

    public Object get(int index)
    {
        return data[index];
    }

    public void set(int index, Object obj)
    {
        data[index] = obj;
    }

    public boolean isPresent(Object obj)
    {
        for (int i = 0; i < length; i++)
        {
            if (data[i].equals(obj))
            {
                return true;
            }
        }

        return false;
    }

    public int getIndex(Object obj)
    {
        for (int i = 0; i < length; i++)
        {
            if (data[i].equals(obj))
            {
                return i;
            }
        }

        return -1;
    }

    private void expand()
    {
        Object[] tempData = new Object[length + 1];

        for (int i = 0; i < length; i++)
        {
            tempData[i] = data[i];
        }

        length++;
        data = tempData;
    }
}
