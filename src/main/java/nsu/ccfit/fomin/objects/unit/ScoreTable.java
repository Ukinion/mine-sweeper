package nsu.ccfit.fomin.objects.unit;

import java.io.*;

import nsu.ccfit.fomin.exception.*;
import javafx.util.Pair;


import java.util.Comparator;
import java.util.TreeSet;

public class ScoreTable {
    private static final String SCORE_FILE_NAME = "score.bin";
    private static final int CLEAR_RESOURCE_FAILED = -2;
    private static final int FILE_EMPTY = 0;

    private static final int DONT_ADD = 0;
    private static final int SORT_UP = 1;
    private static final int SORT_DOWN = -1;

    private static class ScoreTableComparator implements Comparator<Pair<String, Integer>>, Serializable {
        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            if (o1.getKey().equals(o2.getKey())) return DONT_ADD;
            if (o1.getValue() > o2.getValue()) return SORT_DOWN;
            else return SORT_UP;
        }
    }

    private final File _scoreFile;
    private TreeSet<Pair<String, Integer>> _scoreTable;

    public ScoreTable() {
        _scoreFile = new File(SCORE_FILE_NAME);
        try {
            setupScoreTable();
        } catch (IOException | GameException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void setupScoreTable() throws IOException, GameException {
        boolean isFileNotExist = _scoreFile.createNewFile();
        if (!isFileNotExist && _scoreFile.length() != FILE_EMPTY) {
            deserializeScoreTable();
        } else {
            _scoreTable = new TreeSet<>(new ScoreTableComparator());
        }
    }

    @SuppressWarnings("unchecked")
    private void deserializeScoreTable() throws GameException {
        try (FileInputStream inputStream = new FileInputStream(_scoreFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            _scoreTable = (TreeSet<Pair<String, Integer>>) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            throw new DeserializeFailureException();
        }
    }

    public void serializeScoreTable() throws GameException {
        if (_scoreTable.isEmpty()) {
            try {
                clearFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            return;
        }
        FileOutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = new FileOutputStream(_scoreFile);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(_scoreTable);
            objectOutputStream.flush();
        } catch (IOException ex) {
            System.out.println(_scoreFile.getAbsolutePath());
            ex.printStackTrace();
            throw new SerializeFailureException();
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (objectOutputStream != null) objectOutputStream.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(CLEAR_RESOURCE_FAILED);
            }
        }
    }

    private void clearFile() throws IOException, GameException{
        if (!(_scoreFile.delete() && _scoreFile.createNewFile())) {
            throw new FileClearErrorException();
        }
    }

    public void updateScoreTable(String name, Integer score) {
        if (!_scoreTable.add(new Pair<>(name, score))) {
            System.out.println("\tEntered player already exists");
            System.err.println("\tPlease, use another name");
        }
    }

    public void removeFromScoreTable(String playerName) throws GameException {
        boolean isExist = false;
        Pair<String, Integer> player;
        var tableIterator = _scoreTable.iterator();
        while (tableIterator.hasNext()) {
            player = tableIterator.next();
            if (player.getKey().equals(playerName)) {
                isExist = true;
                tableIterator.remove();
                break;
            }
        }
        if (!isExist) {
            throw new RemoveFailureException();
        }
    }

    public TreeSet<Pair<String, Integer>> getScoreTable() {
        return _scoreTable;
    }
}
