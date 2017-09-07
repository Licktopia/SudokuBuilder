import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by do_de on 4/4/2017.
 */
public class SudokuBuilder {

    public static class SudokuSolver {

        Vector<Integer> NumberList;
        Vector<Integer> XCoord;
        Vector<Integer> YCoord;
        Vector<Boolean> MakeUnmake;

        public Vector<Integer> getNumberList() {
            return NumberList;
        }

        public Vector<Integer> getXCoord() {
            return XCoord;
        }

        public Vector<Integer> getYCoord() {
            return YCoord;
        }

        public Vector<Boolean> getMakeUnmake() {
            return MakeUnmake;
        }

        private class LocationBoard {
            LocationBoard(int row, int col){
                x = row;
                y = col;
            }
            int x;
            int y;
        };

        private class Candidates{
            int nCandidates = 0;
            int[] candidates= new int[9];
        }
        private class Cluster {
            int rowBegin;
            int rowEnd;
            int colBegin;
            int colEnd;
        };

        public class Board {
            public Board() { openSquares = 0; }
            public int[][] contents = new int[9][9]; //this is sudoku puzzle we are solving
            private int openSquares; //num of open spots avail.
            private LocationBoard [] corrLoStor = new LocationBoard[81];//1D index of squares an array of x and y coordinates for a given position
        };
        public LocationBoard[] openSpots = new LocationBoard[81];
        public int correctValues[] = new int[81];
        public int currCorrVal;
        public Board sudokuBoard = new Board();
        public boolean finished;
        public int NUM_OF_ELEMENTS;
        public int MAX_ROWS;
        public int MAX_COLS;
        public int MAX_POSSIBLE;
        public int[] fin;
        boolean finished2=false;
        int total = 0;





        public SudokuSolver(int board[])
        {
            NUM_OF_ELEMENTS = 81;
            MAX_ROWS = 9;
            MAX_COLS = 9;
            MAX_POSSIBLE = 10;
            currCorrVal = -1;
            int p = 0;
            XCoord = new Vector<>();
            YCoord = new Vector<>();
            MakeUnmake = new Vector<>();
            NumberList = new Vector<>();

            //open file and read it into the Board

            for(int row = 0; row < MAX_ROWS; row++)
            {
                for(int col = 0; col < MAX_COLS; col++)
                {
                    sudokuBoard.contents[row][col] = board[p];
                    if(sudokuBoard.contents[row][col] == 0)
                    {

                        openSpots[sudokuBoard.openSquares] = new LocationBoard(row,col);
                        sudokuBoard.openSquares++;

                    }
                    p++;
                }
            }
            finished = false;
        }

        public boolean preCheck(){
            for (int i = 0; i < 9; i++)//for each row, column and cluster - outer of nested loop
            {

                int rowbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty row to fill
                int colbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty column to fill
                int clusterbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty cluster to fill

                //SECTION CONVERTS CLUSTER INTO A FLAT ARRAY

                int edgerow = 0;//Setting Edge parameters
                int edgecol = 0;
                edgecol = 3*((i + 3) % 3);

                if (i == 3 || i == 4 || i ==5) { edgerow += 3; }
                if (i == 6 || i == 7 || i == 8) { edgerow += 6; }

                int val = 0;
                int clusterArray[] = { 0,0,0,0,0,0,0,0,0 };//array that will hold cluster

                for (int m = 0; m < 3; m++)
                {
                    for (int p = 0; p < 3; p++)
                    {
                        //copying current cluster into flat cluster Array
                        clusterArray[val] = sudokuBoard.contents[m + edgerow][p+edgecol];
                        val++;
                    }
                }

                for (int j = 0; j < 9; j++)//puts number 1-9 into bank slot of number
                {
                    if(rowbank[sudokuBoard.contents[i][j]] == 0) {
                        rowbank[sudokuBoard.contents[i][j]] = sudokuBoard.contents[i][j];
                    }
                    else {
                        return false;
                    }
                    if(colbank[sudokuBoard.contents[j][i]] == 0) {
                        colbank[sudokuBoard.contents[j][i]] = sudokuBoard.contents[j][i];
                    }
                    else{
                        return false;
                    }
                    //put value found in clustArrray[j] into empty cluster vector
                    if(clusterbank[clusterArray[j]] == 0) {
                        clusterbank[clusterArray[j]] = clusterArray[j];
                    }
                    else{
                        return false;
                    }
                }
            }
            return true;
        }

        public  boolean solvePuzzle(){
            Candidates mCandidate;
            int[] candidates;
            int nCandidates;
            isSolution();
            if(finished) {
                int count = 0;
                int num[] = new int[81];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        num[count] = sudokuBoard.contents[i][j];
                        count++;
                    }
                }
                fin = num;
                if (finished2 != true) {
                    finished = false;
                    finished2 = true;
                } else {
                    finished2 = true;
                    int b = 0;
                    int num2[] = new int[81];
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            num2[b] = sudokuBoard.contents[i][j];
                            b++;
                        }
                    }
                }
            }


            else
            {


                currCorrVal++;

                mCandidate = findCandidates();

                candidates = mCandidate.candidates;
                nCandidates = mCandidate.nCandidates;
                if (finished2 == true && nCandidates == 0) {
                    currCorrVal--;
                }
                for(int i = 0; i < nCandidates; i++)
                {
                    correctValues[currCorrVal] = candidates[i];
                    makeMove();
                    solvePuzzle();
                    if (finished2==true&&finished==true) {
                        for(int h =0;h<9;h++){
                            for(int k = 0; k <9; k++){
                                if(sudokuBoard.contents[h][k]==0){
                                    return true;
                                }
                            }

                        }
                        return false;
                    } //we are done! Solution has been found
                    else
                    {
                        unmakeMove();
                        if(currCorrVal > 0) {
                            if((i+1)>=nCandidates||finished2!=true) {
                                currCorrVal--;
                            }
                        }
                    }
                }

            }

            for(int h =0;h<9;h++){
                for(int k = 0; k <9; k++){
                    if(sudokuBoard.contents[h][k]==0){
                        return true;
                    }
                }

            }
            return false;

        }
        public boolean isSolution(){
            if (sudokuBoard.openSquares != 0) { return false; }//if the board isn't full
            else//if the board is full
            {
                for (int i = 0; i < 9; i++)//for each row, column and cluster - outer of nested loop
                {

                    int rowbank[] = { 0,0,0,0,0,0,0,0,0 };//create new empty row to fill
                    int colbank[] = { 0,0,0,0,0,0,0,0,0 };//create new empty column to fill
                    int clusterbank[] = { 0,0,0,0,0,0,0,0,0 };//create new empty cluster to fill

                    //SECTION CONVERTS CLUSTER INTO A FLAT ARRAY

                    int edgerow = 0;//Setting Edge parameters
                    int edgecol = 0;
                    edgecol = 3*((i + 3) % 3);

                    if (i == 3 || i == 4 || i ==5) { edgerow += 3; }
                    if (i == 6 || i == 7 || i == 8) { edgerow += 6; }

                    int val = 0;
                    int clusterArray[] = { 0,0,0,0,0,0,0,0,0 };//array that will hold cluster

                    for (int m = 0; m < 3; m++)
                    {
                        for (int p = 0; p < 3; p++)
                        {
                            //copying current cluster into flat cluster Array
                            clusterArray[val] = sudokuBoard.contents[m + edgerow][p+edgecol];
                            val++;
                        }
                    }

                    for (int j = 0; j < 9; j++)//puts number 1-9 into bank slot of number
                    {
                        if(sudokuBoard.contents[i][j]!=0) {
                            rowbank[sudokuBoard.contents[i][j] - 1] = sudokuBoard.contents[i][j];
                        }
                        if(sudokuBoard.contents[j][i]!=0) {
                            colbank[sudokuBoard.contents[j][i] - 1] = sudokuBoard.contents[j][i];
                        }
                        //put value found in clustArrray[j] into empty cluster vector
                        if(clusterArray[j]!=0) {
                            clusterbank[clusterArray[j] - 1] = clusterArray[j];
                        }
                    }

                    for (int z = 0; z < 9; z++)//loop through bank vectors
                    {
                        if (rowbank[z] != z + 1 || colbank[z] != z + 1 || clusterbank[z] != z + 1)
                        {//if rowbank slot j doesn't = j, then false
                            return false;//then return false
                        }
                    }
                }
            }
            finished = true;
            return true;

        }
        public Candidates findCandidates()
        {
            Candidates mCandidate = new Candidates();
            int row, col; // Will store position of next move
            LocationBoard locationBoard;

            boolean possible[] = new boolean[10]; //what is possible for the current square
            Arrays.fill(possible, true);
            //memset(possible, true, MAX_POSSIBLE); //set all values to true for later

            //which square should we fill next?
            locationBoard = findNextSquare();
            row = locationBoard.x;
            col = locationBoard.y;

            //store the current row and column into the correct location storage
            sudokuBoard.corrLoStor[currCorrVal] = new LocationBoard(row, col);



            int nCandidates = 0;


            if(row < 0 && col < 0) { return null; } //no moves possible, abort

            //What are the possible values for this square?
            possible = findPossibilites(row, col);


            //update nCandidates and Candidate array
            for(int i = 1; i <= 9; i++)
            {
                if(possible[i] == true)

                {
                    mCandidate.nCandidates++;
                    mCandidate.candidates[nCandidates] = i;
                    nCandidates++;
                }
            }
            return mCandidate;
        }

        public boolean[] findPossibilites (int row, int col){
            //check current row
            boolean possible[] = new boolean[10];
            Arrays.fill(possible, true);
            for(int i = 0; i < 9; i++)
            {
                if(sudokuBoard.contents[row][i] != 0)
                {
                    possible[sudokuBoard.contents[row][i]] = false;
                }
            }

            //check current column
            for(int i = 0; i < 9; i++ )
            {
                if(sudokuBoard.contents[i][col] != 0)
                {
                    possible[sudokuBoard.contents[i][col]] = false;
                }
            }

            //check current cluster
            Cluster cluster = new Cluster();
            int rowEnd, rowBegin, colEnd, colBegin;;
            cluster = getCluster(row, col);
            rowBegin = cluster.rowBegin;
            rowEnd = cluster.rowEnd;
            colBegin = cluster.colBegin;
            colEnd = cluster.colEnd;


            for(int i = rowBegin; i < rowEnd; i++)
            {
                for(int j = colBegin; j < colEnd; j++)
                {
                    if(sudokuBoard.contents[i][j] != 0)
                    {
                        possible[sudokuBoard.contents[i][j]] = false;

                    }
                }
            }
            return possible;
        }
        public Cluster getCluster(int row, int col){
            Cluster cluster = new Cluster();
            if(row <= 2)
            {
                cluster.rowEnd = 3;
                cluster.rowBegin = 0;
                if(col <=2)
                {
                    cluster.colBegin = 0;
                    cluster.colEnd = 3;
                }
                else if(col >=3 && col <= 5)
                {
                    cluster.colBegin = 3;
                    cluster.colEnd = 6;
                }
                else if(col >= 6 && col <= 8)
                {
                    cluster.colBegin = 6;
                    cluster.colEnd = 9;
                }
            }

            else if(row >= 3 && row <= 5)
            {
                cluster.rowEnd = 6;
                cluster.rowBegin = 3;
                if(col <= 2)
                {
                    cluster.colBegin = 0;
                    cluster.colEnd = 2;
                }
                else if(col >= 3 && col <= 5)
                {
                    cluster.colBegin = 3;
                    cluster.colEnd = 6;
                }
                else if(col >= 6 && col <= 8)
                {
                    cluster.colBegin = 6;
                    cluster.colEnd = 9;
                }
            }

            else if(row >= 6 && row <= 8)
            {
                cluster.rowBegin = 6;
                cluster.rowEnd = 9;
                if(col <= 2)
                {
                    cluster.colBegin = 0;
                    cluster.colEnd = 3;
                }
                else if(col >= 3 && col <= 5)
                {
                    cluster.colBegin = 3;
                    cluster.colEnd = 6;
                }
                else if(col >= 6 && col <= 8)
                {
                    cluster.colBegin = 6;
                    cluster.colEnd = 9;
                }
            }
            return cluster;

        }

        public void makeMove(){
            int correctRow = sudokuBoard.corrLoStor[currCorrVal].x;
            int correctCol = sudokuBoard.corrLoStor[currCorrVal].y;
            sudokuBoard.contents[correctRow][correctCol] = correctValues[currCorrVal];
            sudokuBoard.openSquares--;
            XCoord.add(correctRow);
            YCoord.add(correctCol);
            NumberList.add(correctValues[currCorrVal]);
            MakeUnmake.add(true);
           /* if(finished2) {
                total++;

                System.out.print("Make Move\n");
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        System.out.print(sudokuBoard.contents[i][j] + " ");
                    }
                    System.out.print("\n");
                }
                System.out.print("\n");
            }*/

        }
        void unmakeMove(){
            int correctRow = sudokuBoard.corrLoStor[currCorrVal].x;
            int correctCol = sudokuBoard.corrLoStor[currCorrVal].y;
            sudokuBoard.contents[correctRow][correctCol] = 0;
            sudokuBoard.openSquares++;
            XCoord.add(correctRow);
            YCoord.add(correctCol);
            NumberList.add(0);
            MakeUnmake.add(false);
            /*if(finished2) {
                total++;
                System.out.print("UnMake Move\n");
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        System.out.print(sudokuBoard.contents[i][j] + " ");
                    }
                    System.out.print("\n");
                }
                System.out.print("\n");
            }*/
        }
        public LocationBoard findNextSquare(){
            int rowzeros[] = new int[9];
            int colzeros[] = new int[9];
            int clusterzeros[] = new int[9];

            //SECTION GETS AND STORES TOTAL ZEROS FOR EACH ROW, COLUMN AND CLUSTER
            for (int i = 0; i < 9; i++)//for each row, column and cluster - outer of nested loop
            {
                //SECTION CONVERTS CLUSTER INTO A FLAT ARRAY
                int edgerow = 0;
                int edgecol = 0;
                edgecol = 3 * ((i + 3) % 3);

                if (i == 3 || i == 4 || i == 5)
                { edgerow += 3; }
                if (i == 6 || i == 7 || i == 8)
                { edgerow += 6; }

                int val = 0;
                int clusterArray[] = { 0,0,0,0,0,0,0,0,0 };

                for (int m = 0; m < 3; m++)
                {
                    for (int p = 0; p < 3; p++)
                    {
                        clusterArray[val] = sudokuBoard.contents[m + edgerow][p + edgecol];
                        val++;
                    }
                }
                //END CONVERSION

                int rowcount = 0;//counts zeros in row
                int colcount = 0;
                int cluscount = 0;

                for (int j = 0; j < 9; j++)//process through row, column and cluster  - inner of nest loop
                {
                    //if spot in row is 0, add to 0 counter for row
                    if (sudokuBoard.contents[i][j] == 0) { rowcount++; }
                    if (sudokuBoard.contents[j][i] == 0) { colcount++; }
                    if (clusterArray[j] == 0) { cluscount++; }
                }

                rowzeros[i] = rowcount;//i.e. set row [1] to rowcount of row 1
                colzeros[i] = colcount;
                clusterzeros[i] = cluscount;

            }
            //END GET AND STORE TOTAL ZEROS FOR ROW, COLUMN, AND CLUSTER
            //BEGIN FIND 0 SQUARE WITH LEAST ZERO VALUE
            int minzeros = 81;//initialize min zeros
            LocationBoard bestSquare = new LocationBoard(0,0);//initialize best square
            int cluster = 0;//cluster tracker

            for (int k = 0; k < 9; k++)//outer loop, loop through board rows
            {
                //edge tracking for cluster
                if ((k != 0) && ((k % 3) == 0)) { cluster += 3; }
                //loop through board columns
                for (int h = 0; h < 9; h++)
                {
                    //edge tracking for cluster
                    if (h != 0 && h % 3 == 0)	{	cluster++; }

                    int totalzeros = 0;//initialize total zeros counter
                    int samezeros = 0;//initialze same zeros counter (to not double count 0s from the same cluster, if in the same row/col)
                    if (sudokuBoard.contents[k][h] == 0)//if the spot on the board is a zero
                    {
                        if ((h + 4) % 3 == 1)//get same zeros
                        {
                            if (sudokuBoard.contents[k][h+1] == 0) {	samezeros++;}
                            if (sudokuBoard.contents[k][h+2] == 0) {	samezeros++;}
                        }
                        else if ((h + 4) % 3 == 2)//get same zeros
                        {
                            if (sudokuBoard.contents[k][h + 1] == 0) { samezeros++;}
                            if (sudokuBoard.contents[k][h - 1] == 0) { samezeros++;}
                        }
                        else //get same zeros
                        {
                            if (sudokuBoard.contents[k][h - 1] == 0) { samezeros++;}
                            if (sudokuBoard.contents[k][h - 2] == 0) {	samezeros++;}
                        }
                        if ((k + 4) % 3 == 1)//get same zeros
                        {
                            if (sudokuBoard.contents[k + 1][h] == 0) {samezeros++;}
                            if (sudokuBoard.contents[k + 2][h] == 0) {samezeros++;}
                        }
                        else if ((k + 4) % 3 == 2)//get same zeros
                        {
                            if (sudokuBoard.contents[k + 1][h] == 0) {samezeros++;}
                            if (sudokuBoard.contents[k - 1][h] == 0) {samezeros++;}
                        }
                        else//get same zeros
                        {
                            if (sudokuBoard.contents[k - 1][h] == 0) {samezeros++;}
                            if (sudokuBoard.contents[k - 2][h] == 0) {samezeros++;}
                        }
                        totalzeros = rowzeros[k] + colzeros[h] + clusterzeros[cluster] - samezeros -3;//calculates how many other 0s are in the same row, column, and cluster,
                        if (totalzeros < minzeros)//if minimum zeros found so far is less than total zeros													  //not counting itself or other 0s in the same row and column of the cluster
                        {
                            minzeros = totalzeros;//set minzeros to total zeros
                            bestSquare.x = k;//set bestSquare to the current location
                            bestSquare.y = h;
                        }
                    }
                }

                cluster = cluster - 2;
            }

            return bestSquare;

        }





    };






    public static boolean preCheck(int[][] array){
        for (int i = 0; i < 9; i++)//for each row, column and cluster - outer of nested loop
        {

            int rowbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty row to fill
            int colbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty column to fill
            int clusterbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty cluster to fill

            //SECTION CONVERTS CLUSTER INTO A FLAT ARRAY

            int edgerow = 0;//Setting Edge parameters
            int edgecol = 0;
            edgecol = 3*((i + 3) % 3);

            if (i == 3 || i == 4 || i ==5) { edgerow += 3; }
            if (i == 6 || i == 7 || i == 8) { edgerow += 6; }

            int val = 0;
            int clusterArray[] = { 0,0,0,0,0,0,0,0,0 };//array that will hold cluster

            for (int m = 0; m < 3; m++)
            {
                for (int p = 0; p < 3; p++)
                {
                    //copying current cluster into flat cluster Array
                    clusterArray[val] = array[m + edgerow][p+edgecol];
                    val++;
                }
            }

            for (int j = 0; j < 9; j++)//puts number 1-9 into bank slot of number
            {
                if(rowbank[array[i][j]] == 0) {
                    rowbank[array[i][j]] = array[i][j];
                }
                else {
                    return false;
                }
                if(colbank[array[j][i]] == 0) {
                    colbank[array[j][i]] = array[j][i];
                }
                else{
                    return false;
                }
                //put value found in clustArrray[j] into empty cluster vector
                if(clusterbank[clusterArray[j]] == 0) {
                    clusterbank[clusterArray[j]] = clusterArray[j];
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }

    public static int numberOfCandidates(int i, int j, int[][] sudokuBoard){
        int edgerow;
        int edgecol;
        if(i<=2){
            edgerow=0;
        }
        else if(i<=5){
            edgerow=3;
        }
        else{
            edgerow=6;
        }
        if(j<=2){
            edgecol=0;
        }
        else if(j<=5){
            edgecol=3;
        }
        else{
            edgecol=6;
        }

        int val = 0;
        int clusterArray[] = { 0,0,0,0,0,0,0,0,0 };//array that will hold cluster
        for (int m = 0; m < 3; m++)
        {
            for (int p = 0; p < 3; p++)
            {
                //copying current cluster into flat cluster Array
                clusterArray[val] = sudokuBoard[m + edgerow][p+edgecol];
                val++;
            }
        }




        int[] row = {0,0,0,0,0,0,0,0,0,0};

        for(int t = 0; t <9; t++){
              row[sudokuBoard[i][t]]=sudokuBoard[i][t];
              row[sudokuBoard[t][j]]=sudokuBoard[t][j];
              row[clusterArray[t]]=clusterArray[t];
        }
        int z =1;
        while(z < 10){
            if(row[z]==0){
                return 1;
            }
            z++;
        }
        return 0;

    }


    public static boolean generate(int size){
        int[][] sudokuBoard = new int[9][9]; //this is sudoku puzzle we are solving
        for(int i = 0; i<9;i++){
            for(int j=0;j<9;j++){
                sudokuBoard[i][j]=0;
            }
        }


        for(int i = 0; i<9;i++){
            for(int j=0;j<9;j++){
                int nCandidates = numberOfCandidates(i,j,sudokuBoard);
                if(nCandidates==0){

                    return false;
                }
                int randomNum = ThreadLocalRandom.current().nextInt(1, 10);
                sudokuBoard[i][j] = randomNum;
                while(!preCheck(sudokuBoard)) {
                    randomNum = ThreadLocalRandom.current().nextInt(1, 10);
                    sudokuBoard[i][j] = randomNum;
                }

            }
            //System.out.print("\n");
        }


        ArrayList<Integer> takeAway = uniqueRandom(size);
        int[] finalBoard = new int[81];
        int counter = 0;
        for(int i = 0;i<9;i++) {
            for(int j =0;j<9;j++) {

                finalBoard[counter]=sudokuBoard[i][j];
                counter++;

            }
        }
        for(int i =0;i<size;i++) {
            finalBoard[takeAway.get(i)]=0;
        }


        SudokuSolver uniqueSudoku = new SudokuSolver(finalBoard);
        if(!uniqueSudoku.solvePuzzle()){
            //System.out.print("Rejected\n");
            return false;
        }


        for(int i=0;i<81;i++){
            System.out.print(finalBoard[i]);
        }
        System.out.print("\n");
        return true;

    }



    public static ArrayList<Integer> uniqueRandom(int size){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<81; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        for (int i=0; i<size; i++) {
            //System.out.println(list.get(i));
        }
        return list;

    }














    public static void main(String[] args) {
        int boardDiff;
        int numberToGen = 100000;

        for(int i =0;i<numberToGen;i++) {
            boardDiff = ThreadLocalRandom.current().nextInt(25, 46);
            boolean found = false;
            while (!found) {
                found = generate(81-boardDiff);
            }
        }

    }

}
