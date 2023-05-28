package game.model;

import game.exception.GameException;
import game.objects.field.MineField;
import game.objects.unit.Cell;
import game.objects.unit.ScoreTable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

public class MineSweeper {
    public static final String IGNORE_EVENT = "GameTimeOrCellChange";
    public static final String CHANGE_GAME_STAGE_EVENT = "StageChange";
    public static final String REQUEST_ACTION_EVENT = "NothingChange";
    public static final String FIELD_CHANGE_EVENT = "FieldChangeOrFlagSet";

    private static final int BASE = 1;
    private static final int DELAY = 1000;
    private static final int INTERVAL = 1000;
    private static final Object IGNORE = null;
    private static final int START_NEIGHBOUR = -1;
    private static final int END_NEIGHBOUR = 2;

    public String getAboutInfo() {
        return "\tWelcome to Minesweeper!\n\t" +
                "1) Click on field to open cells: \n\t" +
                "\ta) To win you have to open all not mined cells\n\t" +
                "\tb) Use flags to mark places where you think mine is\n\t" +
                "\tc) You lose if you click on mine\n\t" +
                "2) Numbers show count of mines around (3x3 area).\n\t" +
                "3) Try not to blow up!";
    }

    public enum GameStage {
        LAUNCHED, ACTION, VICTORY, DEFEAT, CLOSED
    }

    private GameStage _gameStage;
    private MineField _gameField;
    private ScoreTable _scoreTable;

    private Timer _gameTimer;
    private PropertyChangeSupport _gameEventSupport;

    private int _curScore;
    private int _scoreMultiplier;
    private int _curGameTime;
    private int _cellToOpen;
    private int _numAvailableFlags;
    private boolean _isFirstClick = true;

    public MineSweeper() {
        _gameEventSupport = new PropertyChangeSupport(this);
        _scoreTable = new ScoreTable();
    }

    public void initGameObjects(int gameFieldRow, int gameFieldCol, int numMinesOnField) throws GameException {
        _gameField = new MineField(gameFieldRow, gameFieldCol, numMinesOnField);
        _gameTimer = new Timer();
        _curGameTime = 0;
        _numAvailableFlags = numMinesOnField;
        _curScore = BASE;
        _scoreMultiplier = BASE;
        _gameStage = GameStage.LAUNCHED;
        _cellToOpen = _gameField.getNumClearGround();
    }

    public void startGame() {
        startGameTimer();
        changeStageAndNotify(GameStage.ACTION);
    }

    public void addListener(PropertyChangeListener newListener) {
        _gameEventSupport.addPropertyChangeListener(newListener);
    }

    private void changeStageAndNotify(GameStage newStage) {
        _gameStage = newStage;
        _gameEventSupport.firePropertyChange(CHANGE_GAME_STAGE_EVENT, IGNORE, _gameStage);
    }

    public void clickOnCell(int x, int y) {
        if (isIgnoreClick(x,y)) {
            _gameEventSupport.firePropertyChange(FIELD_CHANGE_EVENT, IGNORE, IGNORE);
        } else {
            processClick(_gameField.locateCell(x,y));
        }
    }

    private boolean isIgnoreClick(int x, int y) {
        return isClickOutOfField(x,y) || isClickOnFlagOrOpenCell(_gameField.locateCell(x,y));
    }

    private boolean isClickOutOfField(int x, int y) {
        return x >= _gameField.getFieldCol() || y >= _gameField.getFieldRow();
    }

    private boolean isClickOnFlagOrOpenCell(Cell cell) {
        return cell.isOpened() || cell.isFlag();
    }

    private void processClick(Cell cell) {
        if (cell.isMine()) {
            if (_isFirstClick) {
                startGameTimer();
                _gameField.moveMine(cell);
                _isFirstClick = false;
            } else {
                Defeat();
                return;
            }
        }
        if (_isFirstClick) {
            _isFirstClick = false;
        }
        openCellLocality(cell.getCoordinateX(), cell.getCoordinateY());
        if (!isVictory()) {
            _gameEventSupport.firePropertyChange(FIELD_CHANGE_EVENT, IGNORE, cell);
        } else {
            changeStageAndNotify(GameStage.VICTORY);
        }
    }

    public void startGameTimer() {
        TimerTask gameTimerTask = new TimerTask() {
            @Override
            public void run() {
                updateCurGameScore();
                _gameEventSupport.firePropertyChange(IGNORE_EVENT,
                        IGNORE, _curGameTime);
            }
        };
        _gameTimer.schedule(gameTimerTask, DELAY, INTERVAL);
    }

    private void updateCurGameScore() {
        _curScore += _scoreMultiplier / (++_curGameTime);
    }

    private void Defeat() {
        _gameField.openField();
        resetGameTimer();
        changeStageAndNotify(GameStage.DEFEAT);
    }

    public void resetGameTimer() {
        _gameTimer.cancel();
        _gameTimer.purge();
    }

    private void openCellLocality(int x, int y) {
        if (_gameField.isOutOfField(x, y)) return;

        Cell cell = _gameField.locateCell(x, y);
        if (cell.isGetRound()) return;

        cell.getRound();
        if (cell.isMine() || cell.isOpened()) return;

        cell.openCell();
        _cellToOpen--;
        _scoreMultiplier++;
        if (cell.getMinesAround() != 0 || _cellToOpen == 0) return;

        for (var i = START_NEIGHBOUR; i < END_NEIGHBOUR; ++i) {
            for (var j = START_NEIGHBOUR; j < END_NEIGHBOUR; ++j) {
                openCellLocality(x-j, y-i);
            }
        }
    }

    private boolean isVictory() {
        if (_cellToOpen == 0) {
            _gameField.openField();
            resetGameTimer();
            changeStageAndNotify(GameStage.VICTORY);
            return true;
        }
        return false;
    }

    public boolean isVictoryStage() {
        return _gameStage == GameStage.VICTORY;
    }


    public ScoreTable getScoreTable() {
        return _scoreTable;
    }

    public MineField getGameField() {
        return _gameField;
    }

    public void useFlag(int x, int y) {
        Cell cell = _gameField.locateCell(x, y);
        if (!cell.isFlag() && _numAvailableFlags == 0 || cell.isOpened()) {
            _gameEventSupport.firePropertyChange(FIELD_CHANGE_EVENT,
                    IGNORE, IGNORE);
            return;
        }

        if (cell.isFlag()) {
            cell.removeFlag();
            ++_numAvailableFlags;
        } else {
            cell.setFlag();
            --_numAvailableFlags;
        }

        if (isVictory()) {
            changeStageAndNotify(GameStage.VICTORY);
        } else {
            _gameEventSupport.firePropertyChange(FIELD_CHANGE_EVENT,
                    IGNORE, IGNORE);
        }
    }

    public int getCurGameTime() {
        return _curGameTime;
    }

    public int getCurScore() {
        return _curScore;
    }

    public int getAvailableFlags() {
        return _numAvailableFlags;
    }

    public void forceCloseGame() {
        resetGameTimer();
        changeStageAndNotify(GameStage.CLOSED);
    }
}
