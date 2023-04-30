package game;

import java.util.Random;
import game.objects.field.MineField;

public class Main
{
    public static void main(String[] argc)
    {
        try {
            MineField mineField = new MineField(2, 2);
            mineField.mineBoard(2);

        } catch (Exception lol)
        {
            System.out.println("lol");
        }
    }
}
