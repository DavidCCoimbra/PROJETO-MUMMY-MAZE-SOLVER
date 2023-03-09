package mummymaze;

import agent.Action;
import agent.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class MummyMazeState extends State implements Cloneable {

    private final char[][] matrix;
    // Heroi
    private int lineHero;
    private int columnHero;
    // Chave
    private int lineKey;
    private int columnKey;
    // Saida
    private int lineExit;
    private int columnExit;
    //Enimigos
    LinkedList<Enemy> enemies;
    // Portão
    LinkedList<Gate> gates;
    //Armadilhas
    LinkedList<Trap> traps;
    //private boolean exitFound;
    private boolean heroKilled;

    public MummyMazeState(char[][] matrix, int lineKey, int columnKey, int lineExit, int columnExit, LinkedList<Gate> gates, LinkedList<Trap> traps) {

        enemies = new LinkedList<>();
        this.gates = gates;
        this.traps = traps;
        this.lineKey = lineKey;
        this.columnKey = columnKey;
        this.lineExit = lineExit;
        this.columnExit = columnExit;
        this.matrix = new char[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {  //elementos dinamicos do nivel
            for (int j = 0; j < matrix.length; j++) {
                //System.out.print(matrix[i][j]);
                this.matrix[i][j] = matrix[i][j];
                if (this.matrix[i][j] == 'H') {
                    lineHero = i;
                    columnHero = j;
                    heroKilled = false;
                    //exitFound = false;
                }
                if (this.matrix[i][j] == 'M' || this.matrix[i][j] == 'V' || this.matrix[i][j] == 'E') {
                    enemies.add(new Enemy(i, j, this.matrix[i][j]));
                }
            }
        }
    }

    public char[][] getGoal() {
        return new char[lineExit][columnExit];
    }

    @Override
    public void executeAction(Action action) { //excuta movimento
        action.execute(this);
        firePuzzleChanged(null);
    }

    public boolean canMoveUp(Character character, int line, int column) {
        if (character == 'H') {
            if (matrix[line - 1][column] == 'S') {
                return true;
            }//move se: não tiver uma parede - o portão estiver aberto - não ter uma armadilha na proxima casa - ter uma chave na proxima casa
            return line > 2 && (matrix[line - 1][column] == ' ' || matrix[line - 1][column] == '_') && (matrix[line - 2][column] == '.' || matrix[line - 2][column] == 'C');
        }//restantes elementos: não tiver uma parede - o portão estiver aberto
        return line > 2 && (matrix[line - 1][column] == ' ' || matrix[line - 1][column] == '_');
    }

    public boolean canMoveRight(Character character, int line, int column) {
        if (character == 'H') {
            if (matrix[line][column + 1] == 'S') {
                return true;
            }//move se: não tiver uma parede - o portão estiver aberto - não ter uma armadilha na proxima casa - ter uma chave na proxima casa
            return column < matrix.length - 2 && (matrix[line][column + 1] == ' ' || matrix[line][column + 1] == ')') && (matrix[line][column + 2] == '.' || matrix[line][column + 2] == 'C');
        }//restantes elementos: não tiver uma parede - o portão estiver aberto
        return column < matrix.length - 2 && (matrix[line][column + 1] == ' ' || matrix[line][column + 1] == ')');
    }

    public boolean canMoveDown(Character character, int line, int column) {
        if (character == 'H') {
            if (matrix[line + 1][column] == 'S') {
                return true;
            }//move se: não tiver uma parede - o portão estiver aberto - não ter uma armadilha na proxima casa - ter uma chave na proxima casa
            return line < matrix.length - 2 && (matrix[line + 1][column] == ' ' || matrix[line + 1][column] == '_') && (matrix[line + 2][column] == '.' || matrix[line + 2][column] == 'C');
        }//restantes elementos: não tiver uma parede - o portão estiver aberto
        return line < matrix.length - 2 && (matrix[line + 1][column] == ' ' || matrix[line + 1][column] == '_');
    }

    public boolean canMoveLeft(Character character, int line, int column) {
        if (character == 'H') {
            if (matrix[line][column - 1] == 'S') {
                return true;
            }//move se: não tiver uma parede - o portão estiver aberto - não ter uma armadilha na proxima casa - ter uma chave na proxima casa
            return column > 2 && (matrix[line][column - 1] == ' ' || matrix[line][column - 1] == ')') && (matrix[line][column - 2] == '.' || matrix[line][column - 2] == 'C');
        }//restantes elementos: não tiver uma parede - o portão estiver aberto
        return column > 2 && (matrix[line][column - 1] == ' ' || matrix[line][column - 1] == ')');
    }

    // MOVE o Heroi
    // Move para cima
    public void moveUp() {
        if (matrix[lineHero - 1][columnHero] == 'S') {
            exitFound = true;
            //no nivel 2 só aceita solução se tiver a linha a baixo descomentada
            matrix[lineHero - 1][columnHero] = 'H';
            matrix[lineHero][columnHero] = '.';
            --lineHero;
            return; //a nivel grafico, comentando a linha 114 e 116, o heroi sai do mapa em vez de mudar a escada
        }
        matrix[lineHero - 2][columnHero] = 'H';
        matrix[lineHero][columnHero] = '.';
        lineHero -= 2;

        checkKeyPress(lineHero, columnHero);

        checkTrapPress(); //kinda irrelevante aqui

        for (Enemy enemy : enemies) { //move todos os inimigos
            if (!heroKilled && enemy.isAlive())
                searchHero(enemy);
        }
    }

    // Move para a direita
    public void moveRight() {
        if (matrix[lineHero][columnHero + 1] == 'S') {
            exitFound = true;
            matrix[lineHero][columnHero + 1] = 'H';
            matrix[lineHero][columnHero] = '.';
            ++columnHero;
            return;
        }
        matrix[lineHero][columnHero + 2] = 'H';
        matrix[lineHero][columnHero] = '.';
        columnHero += 2;

        checkKeyPress(lineHero, columnHero);

        checkTrapPress();

        for (Enemy enemy : enemies) {
            if (!heroKilled && enemy.isAlive())
                searchHero(enemy);
        }
    }

    // Move para baixo
    public void moveDown() {
        if (matrix[lineHero + 1][columnHero] == 'S') {
            exitFound = true;
            //no nivel 1 só aceita solução se tiver a linha a baixo descomentada
            matrix[lineHero + 1][columnHero] = 'H';
            matrix[lineHero][columnHero] = '.';
            ++lineHero;
            return;
        }
        matrix[lineHero + 2][columnHero] = 'H';
        matrix[lineHero][columnHero] = '.';
        lineHero += 2;

        checkKeyPress(lineHero, columnHero);
        checkTrapPress();

        for (Enemy enemy : enemies) {
            if (!heroKilled && enemy.isAlive())
                searchHero(enemy);
        }
    }

    // Move para a esquerda
    public void moveLeft() {
        if (matrix[lineHero][columnHero - 1] == 'S') {
            exitFound = true;
            //no nivel 4 só aceita solução se tiver a linha a baixo descomentada
            matrix[lineHero][columnHero - 1] = 'H';
            matrix[lineHero][columnHero] = '.';
            --columnHero;
            return;
        }
        matrix[lineHero][columnHero - 2] = 'H';
        matrix[lineHero][columnHero] = '.';
        columnHero -= 2;

        checkKeyPress(lineHero, columnHero);
        checkTrapPress();

        for (Enemy enemy : enemies) {
            if (!heroKilled && enemy.isAlive())
                searchHero(enemy);
        }
    }

    // Fica no mesmo sitio espera que os inimigos se mexam
    public void idle() {
        for (Enemy enemy : enemies) {
            if (!heroKilled && enemy.isAlive())
                searchHero(enemy);
        }
    }

    public void checkKeyPress(int line, int column) { //se precionado, "inverte" o estado das gates
        if (line == lineKey && column == columnKey) { //verifica se a persona está em cima da chave
            for (Gate gate : gates) {
                switch(matrix[gate.getLine()][gate.getColumn()]){
                    case '_':
                        matrix[gate.getLine()][gate.getColumn()]='=';
                        break;
                    case '=':
                        matrix[gate.getLine()][gate.getColumn()]='_';
                        break;
                    case '"':
                        matrix[gate.getLine()][gate.getColumn()]=')';
                        break;
                    case ')':
                        matrix[gate.getLine()][gate.getColumn()]='"';
                        break;
                }
            }
            return;
        }
        if (matrix[lineKey][columnKey] == '.') { //só para colocar graficamente a chave no mapa depois da persona sair desta casa
            matrix[lineKey][columnKey] = 'C';
        }
    }


    public void checkTrapPress() { //a mesma historia do checkKeyPress
        for (Trap trap : traps) {
            if (trap.getLine() == lineHero && trap.getColumn() == columnHero) {
                heroKilled = true;
                return;
            }
            if (matrix[trap.getLine()][trap.getColumn()] == '.') {
                matrix[trap.getLine()][trap.getColumn()] = 'A';
            }
        }
    }

    public boolean isHeroKilled() {
        return this.heroKilled;
    }

    public boolean isExitFound() {
        return exitFound;
    }

    public double computeTilesOutOfPlace(){
        if (heroKilled)// se o heroi estiver morto, devolve o maior valor possível
            return Double.MAX_VALUE;

        if(exitFound)// se encontrou a saida, devolve 0 pois é a melhor heuristica possivel
            return 0;

        int lineDistance = 0, columnDistance = 0;

        for (Enemy e: enemies) {
            lineDistance += Math.abs(e.getLine() - lineHero);
            columnDistance += Math.abs(e.getColumn() - columnHero);
        }


        return 1/(lineDistance + columnDistance);
    }

    public double computeTileDistances() {
        if (heroKilled)
            return Double.MAX_VALUE;

        if(exitFound)
            return 0;

        int lineDistance = Math.abs(lineExit - lineHero);
        int columnDistance = Math.abs(lineExit - columnHero);

        return lineDistance + columnDistance;
    }

    public double computeTileEuclideanDistance() {
        if (heroKilled)
            return Double.MAX_VALUE;

        if(exitFound)
            return 0;

        return Math.sqrt((Math.abs(lineExit - lineHero))*2 + (Math.abs(lineExit - columnHero))*2); //hipotenusa
    }


    public int getNumLines() {
        return matrix.length;
    }

    public int getNumColumns() {
        return matrix[0].length;
    }

    public char getTileValue(int line, int column) {
        if (!isValidPosition(line, column)) {
            throw new IndexOutOfBoundsException("Invalid position!");
        }
        return matrix[line][column];
    }

    public boolean isValidPosition(int line, int column) {
        return line >= 0 && line < matrix.length && column >= 0 && column < matrix[0].length;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MummyMazeState)) {
            return false;
        }

        MummyMazeState o = (MummyMazeState) other;
        if (matrix.length != o.matrix.length) {
            return false;
        }

        return Arrays.deepEquals(matrix, o.matrix);
    }

    @Override
    public int hashCode() {
        return 97 * 7 + Arrays.deepHashCode(this.matrix);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            buffer.append('\n');
            for (int j = 0; j < matrix.length; j++) {
                buffer.append(matrix[i][j]);
                buffer.append(' ');
            }
        }
        return buffer.toString();
    }

    @Override
    public MummyMazeState clone() {
        return new MummyMazeState(matrix, lineKey, columnKey, lineExit, columnExit, gates, traps);
    }

    //Listeners
    private transient ArrayList<MummyMazeListener> listeners = new ArrayList<MummyMazeListener>(3);

    public synchronized void removeListener(MummyMazeListener l) {
        if (listeners != null && listeners.contains(l)) {
            listeners.remove(l);
        }
    }

    public synchronized void addListener(MummyMazeListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void firePuzzleChanged(MummyMazeEvent pe) {
        for (MummyMazeListener listener : listeners) {
            listener.puzzleChanged(null);
        }
    }

    public char[][] getMatrix() {
        return this.matrix;
    }

    public int getLineHero() {
        return this.lineHero;
    }

    public int getColumnHero() {
        return this.columnHero;
    }

    private void searchHero(Enemy enemy) {
        //mumia branca      (M) -> coluna -> linha x2
        //mumia vermelha    (V) -> linha -> coluna x2
        //escorpião         (E) -> coluna -> linha x1

        Character character = enemy.getCharacter();

        for (int i = 0; i < 2; i++) {
            if (heroKilled || !enemy.isAlive()) {
                return;
            }
            int line = enemy.getLine();
            int column = enemy.getColumn();

            switch (character) {
                case 'M':
                case 'E':
                    if (columnFirstMoveEnemy(enemy, character, line, column)) {
                        break;
                    } //deixamos como backup, porem não chega a entrar aqui
                    /*if (lineFirstMoveEnemy(enemy, character, line, column)) {// linhas desnecessarias pq não chega a esta fase
                        break;
                    }
                    break;*/
                case 'V':
                    if (lineFirstMoveEnemy(enemy, character, line, column)) {
                        break;
                    }
                    /*if (columnFirstMoveEnemy(enemy, character, line, column)) {
                        break;
                    }*/
                    break;
            }
            if (character == 'E') {
                //apenas se mexe uma vez
                return;
            }

        }
    }


    private boolean columnFirstMoveEnemy(Enemy enemy, Character character, int line, int column) {
        if (columnHero != column) { //ve se está numa coluna diferente
            if (columnHero > column && canMoveRight(character, line, column)) {
                enemy.moveRight();
                return true;
            }
            if (columnHero < column && canMoveLeft(character, line, column)) {
                enemy.moveLeft();
                return true;
            }
        } else { //se coluna igual, move na linha
            if (lineHero > line && canMoveDown(character, line, column)) {
                enemy.moveDown();
                return true;
            }
            if (lineHero < line && canMoveUp(character, line, column)) {
                enemy.moveUp();
                return true;
            }
        }
        return false;
    }

    private boolean lineFirstMoveEnemy(Enemy enemy, Character character, int line, int column) {
        if (lineHero != line) {//ve se está numa linha diferente
            if (lineHero > line && canMoveDown(character, line, column)) {
                enemy.moveDown();
                return true;
            }
            if (lineHero < line && canMoveUp(character, line, column)) {
                enemy.moveUp();
                return true;
            }
        } else { //se linha igual, move na coluna
            if (columnHero > column && canMoveRight(character, line, column)) {
                enemy.moveRight();
                return true;
            }
            if (columnHero < column && canMoveLeft(character, line, column)) {
                enemy.moveLeft();
                return true;
            }
        }
        return false;
    }

    //classe dentro da classe para ser mais fácil modificar os elementos da matrix
    public class Enemy {
        private int line;
        private int column;
        private final Character character;
        private boolean alive;

        public Enemy(int line, int column, Character character) {
            this.line = line;
            this.column = column;
            this.character = character;
            this.alive = true;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        public Character getCharacter() {
            return character;
        }

        public void moveUp() {
            if (matrix[this.line - 2][this.column] == 'H') {
                matrix[this.line - 2][this.column] = this.character;
                matrix[this.line][this.column] = '.';
                this.line -= 2;
                heroKilled = true;
                return;
            }
            if (matrix[this.line - 2][this.column] == 'M' || matrix[this.line - 2][this.column] == 'V' || matrix[this.line - 2][this.column] == 'E') { //verifca se está outro inimigo
                if (matrix[this.line - 2][this.column] == 'E') { // se for escorpiao, mata o escorpiao na casa para onde se vai mover
                    matrix[this.line - 2][this.column] = this.character;
                    matrix[this.line][this.column] = '.';
                    for (Enemy enemy : enemies) {//mudamos o estado do escopiao que foi morto
                        if (enemy.getLine() == this.line - 2 && enemy.getColumn() == this.column && enemy.getCharacter() == 'E') {
                            enemy.setAlive(false); //ou this.alive = false;
                        }
                    }

                    this.line -= 2;
                    return;
                }
                if (this.character == 'E') { //duplicação de codigo
                    matrix[this.line][this.column] = '.';
                    this.alive = false;
                    return;
                }
                matrix[this.line][this.column] = '.';
                this.alive = false;
                return;
            }
            matrix[this.line - 2][this.column] = this.character;
            matrix[this.line][this.column] = '.';
            this.line -= 2;

            checkKeyPress(this.line, this.column);
            checkTrapPress();
        }

        // Move para a direita
        public void moveRight() {
            if (matrix[this.line][this.column + 2] == 'H') {
                matrix[this.line][this.column + 2] = this.character;
                matrix[this.line][this.column] = '.';
                this.column += 2;
                heroKilled = true;
                return;
            }
            if (matrix[this.line][this.column + 2] == 'M' || matrix[this.line][this.column + 2] == 'V' || matrix[this.line][this.column + 2] == 'E') {
                if (matrix[this.line][this.column + 2] == 'E') {
                    matrix[this.line][this.column + 2] = this.character;
                    matrix[this.line][this.column] = '.';
                    for (Enemy enemy : enemies) {
                        if (enemy.getLine() == this.line && enemy.getColumn() == this.column + 2 && enemy.getCharacter() == 'E') {
                            enemy.setAlive(false);
                        }
                    }

                    this.column += 2;
                    return;
                }
                if (this.character == 'E') {
                    matrix[this.line][this.column] = '.';
                    this.alive = false;
                    return;
                }
                matrix[this.line][this.column] = '.';
                this.alive = false;
                return;
            }
            matrix[this.line][this.column + 2] = this.character;
            matrix[this.line][this.column] = '.';
            this.column += 2;

            checkKeyPress(this.line, this.column);
            checkTrapPress();
        }

        // Move para baixo
        public void moveDown() {
            if (matrix[this.line + 2][this.column] == 'H') {
                matrix[this.line + 2][this.column] = this.character;
                matrix[this.line][this.column] = '.';
                this.line += 2;
                heroKilled = true;
                return;
            }
            if (matrix[this.line + 2][this.column] == 'M' || matrix[this.line + 2][this.column] == 'V' || matrix[this.line + 2][this.column] == 'E') {
                if (matrix[this.line + 2][this.column] == 'E') {
                    matrix[this.line + 2][this.column] = this.character;
                    matrix[this.line][this.column] = '.';
                    for (Enemy enemy : enemies) {
                        if (enemy.getLine() == this.line + 2 && enemy.getColumn() == this.column && enemy.getCharacter() == 'E') {

                            enemy.setAlive(false);
                        }
                    }
                    this.line += 2;
                    return;
                }
                if (this.character == 'E') {
                    matrix[this.line][this.column] = '.';
                    this.alive = false;
                    return;
                }
                matrix[this.line][this.column] = '.';
                this.alive = false;
                return;
            }
            matrix[this.line + 2][this.column] = this.character;
            matrix[this.line][this.column] = '.';
            this.line += 2;

            checkKeyPress(this.line, this.column);
            checkTrapPress();
        }

        // Move para a esquerda
        public void moveLeft() {
            if (matrix[this.line][this.column - 2] == 'H') {
                matrix[this.line][this.column - 2] = this.character;
                matrix[this.line][this.column] = '.';
                this.column -= 2;
                heroKilled = true;
                return;
            }
            if (matrix[this.line][this.column - 2] == 'M' || matrix[this.line][this.column - 2] == 'V' || matrix[this.line][this.column - 2] == 'E') {
                if (matrix[this.line][this.column - 2] == 'E') {
                    matrix[this.line][this.column - 2] = this.character;
                    matrix[this.line][this.column] = '.';
                    for (Enemy enemy : enemies) {
                        if (enemy.getLine() == this.line && enemy.getColumn() == this.column - 2 && enemy.getCharacter() == 'E') {
                            enemy.setAlive(false);
                        }
                    }

                    this.column -= 2;
                    return;
                }
                if (this.character == 'E') {
                    matrix[this.line][this.column] = '.';
                    this.alive = false;
                    return;
                }
                matrix[this.line][this.column] = '.';
                this.alive = false;
                return;
            }
            matrix[this.line][this.column - 2] = this.character;
            matrix[this.line][this.column] = '.';
            this.column -= 2;

            checkKeyPress(this.line, this.column);
            checkTrapPress();
        }

        @Override
        public String toString() {
            return "Enemy{" +
                    "character=" + character +
                    '}';
        }

        public boolean isAlive() {
            return alive;
        }

        private void setAlive(boolean b) {
            this.alive = b;
        }
    }
}


