package crawler;

public class IsInCycleException extends Exception
{
    IsInCycleException() {}

    IsInCycleException(String message)
    {
        super(message);
    }
}
