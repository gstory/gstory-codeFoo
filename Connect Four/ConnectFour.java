import java.util.*;
import java.io.*;

class ConnectFour{

	public static boolean quit = false;
	public static boolean twop = false;

	public static void main(String args[]){

		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		boolean repeat = false;

		System.out.println();System.out.println();System.out.println();System.out.println();
		System.out.println("Welcome to ConnectFour! \nColumn numbers are listed above and below the game board. \nJust enter the column number to make a move.\nEnter quit at any time to quit.\n");

		String input = "-p";

		while( input.equals("-p") ){
			System.out.print("\nEnter \"-p\" to toggle between playing against the computer and a human player or press any other key to continue. ");
			try{
				input = b.readLine();
			}catch (IOException ioe) {
				System.out.println("IO error!");
				System.exit(1);
			}
			if( input.equals("-p") ){
				if( twop ){
					System.out.println("Two Player mode disabled.");
					twop = false;
				}
				else{
					System.out.println("Two Player mode enabled.");
					twop = true;
				}
			}
		}

		while( !quit ){
			System.out.println();
			System.out.println("Starting new game.");
			System.out.println();

			CFBoard game = play_game(b, twop);
			game.print_board();

			System.out.println(game.get_text_result());

			input = "";

			if( !quit ){
				//System.out.print("Play again (y/n/-p) ? ");
				repeat = false;
				do{
					if( repeat ){
						System.out.print("Please enter a valid command. ");
					}
					System.out.print("Would you like to play again? (y/n/-p) ");
					repeat = true;
					try{
						input = b.readLine();
					} catch (IOException ioe) {
						System.out.println("IO error!");
						System.exit(1);
					}
					if( input.equals("-p") ){
						repeat = false;
						if( twop ){
							System.out.println("Two Player mode disabled.");
							twop = false;
						}
						else{
							System.out.println("Two Player mode enabled.");
							twop = true;
						}
					}
				}  while( !(input.equals("quit") || input.equals("n") || input.equals("y")) );

				if( input.equals("quit") || input.equals("n") ){
					quit = true;
					break;					
				}
			}

		}

		System.out.println();
		System.out.println("Thanks for playing!");
		System.out.println();

	}

		
	
	

	private static CFBoard play_game(BufferedReader b, boolean twoP){

		CFBoard my_board;
		my_board = new CFBoard(8);
		CFBoard.CFAI comp = my_board.create_ai();
		String input;
		int next_move;
		boolean valid = true;

		while( !my_board.game_over() ){
		
//			my_board.print_debugging();
			my_board.print_board();

			if( my_board.get_turn() == 1 || twoP ){

				valid = true;
				next_move = -1;
				try{
					input = b.readLine();
					if( input.equals("quit") ){
						quit = true;
						break;
					}
					next_move = Integer.parseInt(input);
					next_move--;
				} catch (IOException ioe) {
					System.out.println("IO error!");
					System.exit(1);
				} catch (NumberFormatException e) {
					valid = false;
				}

				if( valid ){
					valid = my_board.make_move(next_move);
				}

				if( !valid && !my_board.game_over() )
					System.out.println("Invalid move.");
		
			}
			else{
				comp.move();
			}


						
		}

		return my_board;

	}

}

class CFBoard{

	private final int BOARD_SIZE;
	private int[][] board;
	private int[] heights;
	private boolean gameOver = false;
	private CFAI comp;

	//RED is player
	private final int RED = 1;
	//BLACK is computer
	private final int BLACK = -1;
	private int playerTurn = RED;

	//add a second value for the adjacent Coord?
	private ArrayList< Coord[] > redTwoIAR = new ArrayList< Coord[] >();
	private ArrayList< Coord[] > redThreeIAR = new ArrayList< Coord[] >();
	private ArrayList< Coord > redFourIAR = new ArrayList< Coord >();
	private ArrayList< Coord[] > blackTwoIAR = new ArrayList< Coord[] >();
	private ArrayList< Coord[] > blackThreeIAR = new ArrayList< Coord[] >();
	private ArrayList< Coord > blackFourIAR = new ArrayList< Coord >();

	//RED = 1
	//TIE = 0
	private final int TIE = 0;
	//BLACK = -1
	private int result;
	

	public CFBoard(){
		this.BOARD_SIZE = 8;
		board = new int[BOARD_SIZE][BOARD_SIZE];
		heights = new int[BOARD_SIZE];
	}

	public CFBoard(int size){
		this.BOARD_SIZE = size;
		board = new int[BOARD_SIZE][BOARD_SIZE];
		heights = new int[BOARD_SIZE];
	}

	public int get(Coord toGet){
		return board[toGet.get_row()][toGet.get_col()];
	}

	public int size(){
		return BOARD_SIZE;
	}

	public int get_turn(){
		return playerTurn;
	}

	public void print_board(){

		System.out.print("	");
		for( int i = 1; i <= BOARD_SIZE - 1; i++ )
			System.out.print(i + "|");
		System.out.println(BOARD_SIZE);
		System.out.println();
		
		for( int r = BOARD_SIZE - 1; r >= 0; r-- ){
//			System.out.print("	");
			System.out.print("       |");
			for( int c = 0; c < BOARD_SIZE; c++ ){

				if( board[r][c] == -1 )
					System.out.print("B");
				else if( board[r][c] == 1 )
					System.out.print("r");
				else
					System.out.print(" ");
				
				System.out.print("|");

			}
			System.out.println();
		}

		System.out.println();
		System.out.print("	");
		for( int i = 1; i <= BOARD_SIZE - 1; i++ )
			System.out.print(i + "|");
		System.out.println(BOARD_SIZE);

		System.out.println();
		if( playerTurn == RED && !gameOver )
			System.out.print("RED'S TURN : ");
		else if( playerTurn == BLACK && !gameOver )
			System.out.print("BLACK'S TURN : ");

	}

//DEBUGGING
	public void print_debugging(){

		System.out.println("Debugging: ");
		System.out.println("  redTwoIAR : " + redTwoIAR.size() );
		for( Coord[] pairs : redTwoIAR ){

			System.out.println("    ("+pairs[0].get_row()+","+pairs[0].get_col()+") , ("+pairs[1].get_row()+","+pairs[1].get_col()+")");

		}
		System.out.println("  redThreeIAR : " + redThreeIAR.size() );
		for( Coord[] pairs : redThreeIAR ){

			System.out.println("    ("+pairs[0].get_row()+","+pairs[0].get_col()+") , ("+pairs[1].get_row()+","+pairs[1].get_col()+")");

		}
		System.out.println("  blackTwoIAR : " + blackTwoIAR.size() );
		for( Coord[] pairs : blackTwoIAR ){

			System.out.println("    ("+pairs[0].get_row()+","+pairs[0].get_col()+") , ("+pairs[1].get_row()+","+pairs[1].get_col()+")");

		}
		System.out.println("  blackThreeIAR : " + blackThreeIAR.size() );
		for( Coord[] pairs : blackThreeIAR ){

			System.out.println("    ("+pairs[0].get_row()+","+pairs[0].get_col()+") , ("+pairs[1].get_row()+","+pairs[1].get_col()+")");

		}

	}

	public int next_pos(int height){
		return heights[height];
	}

	//returns false if move is not valid
	public boolean make_move(int col){

		if( col < BOARD_SIZE && heights[col] < BOARD_SIZE ){

			Coord moveMade = new Coord(heights[col], col);
			board[moveMade.get_row()][ moveMade.get_col() ] = playerTurn;
			heights[col] = heights[col] + 1;
			update_IAR_arrays( moveMade );
			
			if( !check_game_over() ){
				playerTurn *= -1;
				return true;
			}
			else
				return false;

		}
		else
			return false;

	}

	//there has to be a more efficient way of doing this
	private void update_IAR_arrays( Coord moveMade ){

		ArrayList<Coord> toUpdate = adjacent(moveMade);
		ArrayList<Coord[]> toRemove;
		Coord tempCo;
		Coord tempCo2;

		if( playerTurn == RED ){

			for( Coord[] pairs : redThreeIAR ){
				if( pairs[0].get_row() == moveMade.get_row() && pairs[0].get_col() == moveMade.get_col() ){
					redFourIAR.add( moveMade );
				}
				if( pairs[1].get_row() == moveMade.get_row() && pairs[1].get_col() == moveMade.get_col() ){
					redFourIAR.add( moveMade );
				} 
			}

			for( Coord[] pairs : redTwoIAR ){
				if( pairs[0].get_row() == moveMade.get_row() && pairs[0].get_col() == moveMade.get_col() ){
					tempCo = opposite_side(pairs[0], pairs[1]);
					tempCo2 = opposite_side(pairs[1], pairs[0]);
					if( chain_length(tempCo, tempCo2) == 3 ){
						redThreeIAR.add( new Coord[]{tempCo, tempCo2} );
						redThreeIAR.add( new Coord[]{tempCo2, tempCo} );
					}
					else if( chain_length(tempCo, tempCo2) >= 4 ){
						redFourIAR.add( tempCo );
					}
				}
				if( pairs[1].get_row() == moveMade.get_row() && pairs[1].get_col() == moveMade.get_col() ){
					tempCo = opposite_side(pairs[1], pairs[0]);
					tempCo2 = opposite_side(pairs[1], pairs[0]);
					if( chain_length(tempCo, tempCo2) == 2 ){
						redTwoIAR.add( new Coord[]{tempCo, tempCo2} );
						redTwoIAR.add( new Coord[]{tempCo2, tempCo} );
					}
					else if( chain_length(tempCo, tempCo2) == 3 ){
						redThreeIAR.add( new Coord[]{tempCo, tempCo2} );
						redThreeIAR.add( new Coord[]{tempCo2, tempCo} );
					}
					else if( chain_length(tempCo, tempCo2) >= 4 ){
						redFourIAR.add( tempCo );
					}
				}
			}

			for( Coord singles : toUpdate ){

				tempCo = opposite_side(singles, moveMade);
				tempCo2 = opposite_side(moveMade, singles);
				if( chain_length(tempCo, tempCo2) == 2 ){
					redTwoIAR.add( new Coord[]{tempCo, tempCo2} );
					redTwoIAR.add( new Coord[]{tempCo2, tempCo} );
				}
				else if( chain_length(tempCo, tempCo2) == 3 ){
					redThreeIAR.add( new Coord[]{tempCo, tempCo2} );
					redThreeIAR.add( new Coord[]{tempCo2, tempCo} );
				}
				else if( chain_length(tempCo, tempCo2) >= 4 ){
					redFourIAR.add( tempCo );
				}
				

			}

		}

		if( playerTurn == BLACK ){

			for( Coord[] pairs : blackThreeIAR ){
				if( pairs[0].get_row() == moveMade.get_row() && pairs[0].get_col() == moveMade.get_col() ){
					blackFourIAR.add( moveMade );
				}
				if( pairs[1].get_row() == moveMade.get_row() && pairs[1].get_col() == moveMade.get_col() ){
					blackFourIAR.add( moveMade );
				} 
			}

			for( Coord[] pairs : redTwoIAR ){
				if( pairs[0].get_row() == moveMade.get_row() && pairs[0].get_col() == moveMade.get_col() ){
					tempCo = opposite_side(pairs[0], pairs[1]);
					tempCo2 = opposite_side(pairs[1], pairs[0]);
					if( chain_length(tempCo, tempCo2) == 2 ){
						blackTwoIAR.add( new Coord[]{tempCo, tempCo2} );
						blackTwoIAR.add( new Coord[]{tempCo2, tempCo} );					
					}
					else if( chain_length(tempCo, tempCo2) == 3 ){
						blackThreeIAR.add( new Coord[]{tempCo, tempCo2} );
						blackThreeIAR.add( new Coord[]{tempCo2, tempCo} );					
					}
					else if( chain_length(tempCo, tempCo2) >= 4 ){
						blackFourIAR.add( tempCo );
					}
				}
				if( pairs[1].get_row() == moveMade.get_row() && pairs[1].get_col() == moveMade.get_col() ){
					tempCo = opposite_side(pairs[1], pairs[0]);
					tempCo2 = opposite_side(pairs[1], pairs[0]);
					if( chain_length(tempCo, tempCo2) == 2 ){
						blackTwoIAR.add( new Coord[]{tempCo, tempCo2} );
						blackTwoIAR.add( new Coord[]{tempCo2, tempCo} );					
					}
					else if( chain_length(tempCo, tempCo2) == 3 ){
						blackThreeIAR.add( new Coord[]{tempCo, tempCo2} );
						blackThreeIAR.add( new Coord[]{tempCo2, tempCo} );					
					}
					else if( chain_length(tempCo, tempCo2) >= 4 ){
						blackFourIAR.add( tempCo );
					}
				}
			}

			for( Coord singles : toUpdate ){

				tempCo = opposite_side(singles, moveMade);
				tempCo2 = opposite_side(moveMade, singles);
				if( chain_length(tempCo, tempCo2) == 2 ){
					blackTwoIAR.add( new Coord[]{tempCo, tempCo2} );
					blackTwoIAR.add( new Coord[]{tempCo2, tempCo} );					
				}
				else if( chain_length(tempCo, tempCo2) == 3 ){
					blackThreeIAR.add( new Coord[]{tempCo, tempCo2} );
					blackThreeIAR.add( new Coord[]{tempCo2, tempCo} );					
				}
				else if( chain_length(tempCo, tempCo2) >= 4 ){
					blackFourIAR.add( tempCo );
				}

			}

		}
		
	}

	private Coord opposite_side(Coord first, Coord second){

		Coord result = first;


		if( board[first.get_row()][first.get_col()] != board[second.get_row()][second.get_col()] || first.equals(second) )
			return first;

		
		int row_offset = 0;
		if( first.get_row() != second.get_row() )
			row_offset = (first.get_row() - second.get_row()) / Math.abs(first.get_row() - second.get_row());
		int col_offset = 0;
		if( first.get_col() != second.get_col() )
			col_offset = (first.get_col() - second.get_col()) / Math.abs(first.get_col() - second.get_col());
		if( first.get_row() + row_offset >= 0 && first.get_col() + col_offset >= 0 && first.get_row() + row_offset < BOARD_SIZE && first.get_col() + col_offset < BOARD_SIZE ){
			while( board[result.get_row()][result.get_col()] == board[first.get_row()][first.get_col()] &&
				result.get_row() + row_offset >= 0 && result.get_col() + col_offset >= 0 && result.get_row() + row_offset < BOARD_SIZE && result.get_col() < BOARD_SIZE)
				result = new Coord(result.get_row() + row_offset, result.get_col() + col_offset);

		}
		return result;

	}

	private int chain_length(Coord first, Coord second){

		if( first.equals(second) )
			return 1;
		Coord val;
		Coord mov;
		int compar;
		int result = 0;

		int row_offset = 0;
		if( first.get_row() != second.get_row() )
			row_offset = (first.get_row() - second.get_row()) / Math.abs(first.get_row() - second.get_row());
		
		int col_offset = 0;
		if( first.get_col() != second.get_col() )
			col_offset = (first.get_col() - second.get_col()) / Math.abs(first.get_col() - second.get_col());

		mov = new Coord(second.get_row() + row_offset, second.get_col() + col_offset);
			
		compar = board[mov.get_row()][mov.get_col()];

		while( board[mov.get_row()][mov.get_col()] == compar && mov.get_row() - row_offset >= 0 && 
				mov.get_col() - col_offset >= 0 && mov.get_row() - row_offset < BOARD_SIZE && mov.get_col() - col_offset < BOARD_SIZE )

			mov = new Coord(mov.get_row() - row_offset, mov.get_col() - col_offset);

		mov = new Coord(mov.get_row() + row_offset, mov.get_col() + col_offset);
		result++;

		while( board[mov.get_row()][mov.get_col()] == compar && mov.get_row() + row_offset >= 0 && 
				mov.get_col() + col_offset >= 0 && mov.get_row() + row_offset < BOARD_SIZE && mov.get_col() + col_offset < BOARD_SIZE ){

			mov = new Coord(mov.get_row() + row_offset, mov.get_col() + col_offset);
			if( board[mov.get_row()][mov.get_col()] == compar )
				result++;

		}

		return result;

	}

	private boolean is_gap(Coord first, Coord second){

		if( get(first) != 1 || get(first) != -1 )
			return false;

		Coord val;
		Coord mov;
		int compar;
		boolean result = false;;

		int row_offset = 0;
		if( first.get_row() != second.get_row() )
			row_offset = (first.get_row() - second.get_row()) / Math.abs(first.get_row() - second.get_row());
		
		int col_offset = 0;
		if( first.get_col() != second.get_col() )
			col_offset = (first.get_col() - second.get_col()) / Math.abs(first.get_col() - second.get_col());

		mov = new Coord(second.get_row() + row_offset, second.get_col() + col_offset);
			
		compar = board[mov.get_row()][mov.get_col()];

		while( board[mov.get_row()][mov.get_col()] == compar && mov.get_row() - row_offset >= 0 && 
				mov.get_col() - col_offset >= 0 && mov.get_row() - row_offset < BOARD_SIZE && mov.get_col() - col_offset < BOARD_SIZE )

			mov = new Coord(mov.get_row() - row_offset, mov.get_col() - col_offset);

		mov = new Coord(mov.get_row() + row_offset, mov.get_col() + col_offset);

		while( board[mov.get_row()][mov.get_col()] == compar && mov.get_row() + row_offset >= 0 && 
				mov.get_col() + col_offset >= 0 && mov.get_row() + row_offset < BOARD_SIZE && mov.get_col() + col_offset < BOARD_SIZE ){

			mov = new Coord(mov.get_row() + row_offset, mov.get_col() + col_offset);
		}

		mov = new Coord(mov.get_row() + row_offset, mov.get_col() + col_offset);

		if( mov.get_row() >= 0 && mov.get_col() >= 0 && mov.get_row() < BOARD_SIZE && mov.get_col() < BOARD_SIZE && get(mov) == compar )
			return true;
		else 
			return false;

	}

	private boolean check_game_over(){

		if( redFourIAR.size() != 0 || blackFourIAR.size() != 0 ){

			gameOver = true;
			result = playerTurn;

		}
		else if( board_full() ){

			gameOver = true;
			result = TIE;
		
		}

		return gameOver;

	}

	private boolean board_full(){

		for( int h : heights ){
			if( h != BOARD_SIZE )
				return false;
		}
		return true;

	}

	private ArrayList<Coord> adjacent(Coord point){

		ArrayList<Coord> results = new ArrayList<Coord>();
		for( int r = -1; r < 2; r++ ){
			
			if( point.get_row() + r < 0 )
				r++;
			if( point.get_row() + r >= BOARD_SIZE )
				break;

			for( int c = -1; c < 2; c++){

				//skip if outside boundaries or on origin
				if( point.get_col() + c < 0 || (c == 0 && r == 0) )
					c++;
				if( point.get_col() + c >= BOARD_SIZE )
					break;

				if( board[point.get_row() + r][point.get_col() + c] == playerTurn )
					results.add( new Coord( (point.get_row() + r), point.get_col() + c ) );
			
			}
		}
		return results;

	}



	public boolean game_over(){
		return gameOver;
	}

	public int get_result(){
		return result;
	}
	public String get_text_result(){

		if( result == RED )
			return "Red wins!";
		else if( result == BLACK )
			return "Black wins!";
		else
			return "Tie game!";

	}
	
	public CFAI create_ai(){

		this.comp = new CFAI();
		return comp;

	}


	/*
	 *  class Connect Four AI 
	 *  
	 */

	class CFAI{

		public CFAI(){
	
		}

		//TODO
		public boolean move(){

			boolean success = false;
			boolean skip = false;
			ArrayList<Integer> priorities = new ArrayList<Integer>();

		//THREE
			if( blackThreeIAR.size() != 0 ){
				for( Coord[] triples : blackThreeIAR ){

					if( heights[triples[0].get_col()] == triples[0].get_row() ){
						priorities.add(triples[0].get_col());
					}

				}
			}
			
			success = try_move( priorities );
			if( success )
				return success;

			
			if( redThreeIAR.size() != 0 ){
				for( Coord[] triples : redThreeIAR ){

					if( heights[triples[0].get_col()] == triples[0].get_row() )
						priorities.add(triples[0].get_col());

				}
			}

			success = try_move( priorities );
			if( success )
				return success;



		//TWO
			if( blackTwoIAR.size() != 0 ){
				for( Coord[] pairs : blackTwoIAR ){

					if( heights[pairs[0].get_col()] == pairs[0].get_row() ){
						skip = false;

						for( Coord[] redTriple : redThreeIAR ){

							if( heights[pairs[0].get_col()] + 1 == redTriple[0].get_row() )
								skip = true;

						}

						if( !skip )
							priorities.add(pairs[0].get_col());
					}

				}
			}

			success = try_move( priorities );
			if( success )
				return success;


			
			if( redTwoIAR.size() != 0 ){
				for( Coord[] redPairs : redTwoIAR ){

					if( heights[redPairs[0].get_col()] == redPairs[0].get_row() ){
		
						if( is_gap( redPairs[0], redPairs[1] ) ){

							priorities.add(redPairs[0].get_col());

						}
						else{

							skip = false;

							for( Coord[] redTriple : redThreeIAR ){

								if( heights[redPairs[0].get_col()] + 1 == redTriple[0].get_row() )
									skip = true;

							}

							if( !skip )
								priorities.add(redPairs[0].get_col());

						}
					}

				}
			}

			success = try_move( priorities );
			if( success )
				return success;
	

			ArrayList<Integer> lastResort = new ArrayList<Integer>();
			ArrayList<Integer> undesirable = new ArrayList<Integer>();

			if( heights[3] > heights[4] ){

				for( Coord[] redTriple : redThreeIAR ){
					if( heights[4] + 1 == redTriple[0].get_row() )
						lastResort.add(4);
					if( heights[3] + 1 == redTriple[0].get_row() )
						lastResort.add(3);
				}
				for( Coord[] redPair : redTwoIAR ){
					if( heights[4] + 1 == redPair[0].get_row() && !lastResort.contains(4) ){
						if( is_gap( redPair[0], redPair[1] ) ){
							priorities.add(redPair[0].get_col());
						}
						else{
							undesirable.add(4);
						}
					}
					if( heights[3] + 1 == redPair[0].get_row() && !lastResort.contains(3) ){
						undesirable.add(3);
					}
				}

				if( !lastResort.contains(4) && !undesirable.contains(4) )
					priorities.add(4);
				if( !lastResort.contains(3) && !undesirable.contains(3) )
					priorities.add(3);
			}
			else{
				for( Coord[] redTriple : redThreeIAR ){
					if( heights[3] + 1 == redTriple[0].get_row() )
						lastResort.add(3);
					if( heights[4] + 1 == redTriple[0].get_row() )
						lastResort.add(4);
				}
				for( Coord[] redPair : redTwoIAR ){
					if( heights[3] + 1 == redPair[0].get_row() && !lastResort.contains(4) ){
						if( is_gap( redPair[0], redPair[1] ) ){
							priorities.add(redPair[0].get_col());
						}
						else{
							undesirable.add(3);
						}
					}
					if( heights[4] + 1 == redPair[0].get_row() && !lastResort.contains(3) ){
						undesirable.add(4);
					}
				}

				if( !lastResort.contains(3) && !undesirable.contains(3) )
					priorities.add(3);
				if( !lastResort.contains(4) && !undesirable.contains(4) )
					priorities.add(4);
			}

			int[] nums = new int[]{2, 5, 1, 6, 0, 7};
			for( int num : nums ){

				for( Coord[] redTriple : redThreeIAR ){
					if( heights[num] + 1 == redTriple[0].get_row() )
						lastResort.add(num);
				}
				for( Coord[] redPair : redTwoIAR ){
					if( heights[num] + 1 == redPair[0].get_row() && !lastResort.contains(num) )
						if( is_gap( redPair[0], redPair[1] ) ){
							priorities.add(num);
						}
						else{
							undesirable.add(num);
						}
				}

				if( !lastResort.contains(num) && !undesirable.contains(num) )
					priorities.add(num);

			}

			success = try_move(priorities);
			if( !success )
				success = try_move(undesirable);
			if( !success )
				success = try_move(lastResort);

			return success;

		}

		private boolean try_move( ArrayList<Integer> priorities ){


			boolean success = false;
			Integer attempt;

			while( priorities.size() != 0 && !gameOver ){
				
				attempt = priorities.get(0);
				System.out.println(attempt+1);
				success = make_move(attempt);
				if( success ) {
					return success;
				}
				else
					priorities.remove(0);
			}

			return success;


		}

	}

}

class Coord{

	private int row;
	private int col;

	public Coord(int row, int col){

		this.row = row;
		this.col = col;

	}

	public int get_row(){

		return row;

	}

	public int get_col(){

		return col;

	}

	public boolean equals(Coord c){

		if( this.row == c.get_row() && this.col == c.get_col() )
			return true;
		else
			return false;

	}

}
