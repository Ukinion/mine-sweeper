package game.objects.unit;

import java.io.*;
import game.exception.DeserializeFailureException;
import game.exception.GameException;
import game.exception.SerializeFailureException;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.TreeSet;

public class ScoreTable
{
    private static final String SCORE_FILE_NAME = "score.bin";
    private static final int FILE_EMPTY = 0;

    private static class ScoreTableComparator implements Comparator<Pair<String, Double>>
    {
        @Override
        public int compare(Pair<String, Double> o1, Pair<String, Double> o2)
        {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

    private final File _scoreFile;
    private TreeSet<Pair<String, Double>> _scoreTable;

    public ScoreTable()
    {
        _scoreFile = new File(SCORE_FILE_NAME);
        try
        {
            setupScoreTable();
        }
        catch (IOException | GameException ex)
        {
            System.err.println(ex.getMessage());
        }
    }

    private void setupScoreTable() throws IOException, GameException
    {
        boolean isFileExist = _scoreFile.createNewFile();
        if (isFileExist && _scoreFile.length() != FILE_EMPTY)
        {
            deserializeScoreTable();
        }
        else
        {
            _scoreTable = new TreeSet<>(new ScoreTableComparator());
        }
    }

    @SuppressWarnings("unchecked")
    private void deserializeScoreTable() throws GameException
    {
        try (FileInputStream inputStream = new FileInputStream(_scoreFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream))
        {
            _scoreTable = (TreeSet<Pair<String, Double>>) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            throw new DeserializeFailureException();
        }
    }

    public void serializeScoreTable() throws GameException
    {
        try (FileOutputStream outputStream = new FileOutputStream(_scoreFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream))
        {
            objectOutputStream.writeObject(_scoreTable);
            objectOutputStream.flush();
        }
        catch (IOException ex)
        {
            throw new SerializeFailureException();
        }
    }

    public void updateScoreTable(String name, Double score)
    {
        _scoreTable.add(new Pair<>(name, score));
    }

    public void removeFromScoreTable(Pair<String, Double> player)
    {
        _scoreTable.remove(player);
    }

    public TreeSet<Pair<String, Double>> getScoreTable()
    {
        return _scoreTable;
    }
}
