package at.phillip.client.utils;

public interface ICommand {
    String getName();
    boolean handle(String message);
}
