package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
//import uk.ac.bris.cs.scotlandyard.model.Move.*;
//import uk.ac.bris.cs.scotlandyard.model.Piece.*;
//import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState>{
	private final class MyGameState implements GameState{
		
		/**
		 * @return the current game setup
		 */
		@Nonnull
		@Override
		public GameSetup getSetup() {return GSFSetup;}

		/**
		 * @return all players in the game
		 */
		@Nonnull
		@Override
		public ImmutableSet<Piece> getPlayers() {
			return null;
		}

		/**
		 * @param detective the detective
		 * @return the location of the given detective; empty if the detective is not part of the game
		 */
		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			return Optional.empty();
		}

		/**
		 * @param piece the player piece
		 * @return the ticket board of the given player; empty if the player is not part of the game
		 */
		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			return Optional.empty();
		}

		/**
		 * @return MrX's travel log as a list of {@link LogEntry}s.
		 */
		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {
			return null;
		}

		/**
		 * @return the winner of this game; empty if the game has no winners yet
		 * This is mutually exclusive with {@link #getAvailableMoves()}
		 */
		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			return null;
		}

		/**
		 * @return the current available moves of the game.
		 * This is mutually exclusive with {@link #getWinner()}
		 */
		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			return null;
		}

		/**
		 * Computes the next game state given a move from {@link #getAvailableMoves()} has been
		 * chosen and supplied as the parameter
		 *
		 * @param move the move to make
		 * @return the game state of which the given move has been made
		 * @throws IllegalArgumentException if the move was not a move from
		 *                                  {@link #getAvailableMoves()}
		 */
		@Nonnull
		@Override
		public GameState advance(Move move) {
			return null;
		}
	}



	//public  ImmutableList<LogEntry> IS_GSFlog;


	ImmutableSet<Piece> winner = ImmutableSet.of();

        @Nonnull @Override public GameState build(
                GameSetup setup,
                Player mrX,
                ImmutableList<Player> detectives) {
            // TODO
            //throw new RuntimeException("Implement me!");

            GameSetup GSFSetup = setup;
            Player GSFmrX = mrX;
            ImmutableList<Player> GSFdetectives = detectives;
            //final ImmutableSet<LogEntry> IS_GSFlog;

            GameState GSFgState = new GameState() {
                @Nonnull
                @Override
                public GameState advance(Move move) {
					GameState newGS;
					/**
					 * Computes the next game state given a move from {@link #getAvailableMoves()} has been
					 * chosen and supplied as the parameter
					 *
					 * @param move the move to make
					 * @return the game state of which the given move has been made
					 * @throws IllegalArgumentException if the move was not a move from
					 * {@link #getAvailableMoves()}
					 */
                    return null;
                }

                @Nonnull
                @Override
                public GameSetup getSetup() {
                    return GSFSetup;
                }

                @Nonnull
                @Override
                public ImmutableSet<Piece> getPlayers() {
                    List<Piece> DP = new ArrayList<>();
                    for(int i = 0; i <GSFdetectives.size(); i++){DP.add(GSFdetectives.get(i).piece());}

                    /*static*/
				final ImmutableSet<Piece> GSFplayers =
						ImmutableSet.<Piece>builder()
								.add(GSFmrX.piece())
								//.addAll( GSFdetectives.iterator() GSFdetectives.piece())
								//GSFdetectives.forEach((p) -> (List<Piece> DP =  p.piece()));
								.addAll(DP)
								.build();
				return GSFplayers;
			}

			@Nonnull
			@Override
			public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
				/**
				 * @param detective the detective
				 * @return the location of the given detective; empty if the detective is not part of the game
				 */
				var ref = new Object() {
					Player id;
				};
				GSFdetectives.forEach((p) -> {
					if(p.piece() == detective ){ref.id = p;} //&&!mrX	??? could just use method piece from player
				});
				if(ref.id == null){return Optional.empty();}
				return Optional.ofNullable(ref.id.location()); // ref.id can't be null, ie when called for non existent player should be empty nt null
			}

			@Nonnull
			@Override
			public Optional<TicketBoard> getPlayerTickets(Piece piece) {
				/**
				 * @param piece the player piece
				 * @return the ticket board of the given player; empty if the player is not part of the game
				 */
				var ref = new Object() {
					Player id = null;
				};

				if(piece.isMrX()){ref.id = mrX;System.out.println("Piece provided is mrx:"+ ref.id);}

				else if (piece.isDetective()) {
					GSFdetectives.forEach((p) -> {
						//id = p.piece();
						if (p.piece() == piece) {ref.id = p;System.out.println("Piece provided is:"+ p);}
					});
				}
				if(ref.id == null){return Optional.empty();}

				TicketBoard GSFTB = new TicketBoard() {
					@Override
					public int getCount(@Nonnull ScotlandYard.Ticket ticket) {
						/**
						 * @param ticket the ticket to check count for
						 * @return the amount of ticket,that player has, always &gt;>= 0
						 */
						return ref.id.tickets().get(ticket);
					}
				};

				return Optional.ofNullable(GSFTB);
			}

			@Nonnull
			@Override
			public ImmutableList<LogEntry> getMrXTravelLog() {
				/**
				 * @return MrX's travel log as a list of {@link LogEntry}s.
				 */
				/*private static*/ ArrayList<LogEntry> GSFlog = new ArrayList<>();

				//LogEntry a = LogEntry.hidden();
				//LogEntry GSFlog = new LogEntry.hidden((mrX.tickets()).getOrDefault(null , ScotlandYard.Ticket()));
				//ImmutableMap<ScotlandYard.Ticket, Integer> tickets
				//mrX.tickets());
				//(@Nonnull
						//ScotlandYard.Ticket ticket)
				//LogEntry GSFlog = new LogEntry.hidden();

				// use builder etc???????


				return ImmutableList.copyOf(GSFlog);
			}

			@Nonnull
			@Override
			public ImmutableSet<Piece> getWinner() {
					/**
				 	* @return the winner of this game; empty if the game has no winners yet
				 	* This is mutually exclusive with {@link #getAvailableMoves()}
				 	*/

				var ref = new Object() {
					int counter = 0;
				};
				GSFdetectives.forEach(p ->{
					if (p.tickets().values().stream()/*.filter(x -> x>0)*/.count() == 0){
						ref.counter = ref.counter + 1;}

					/*while(p.tickets().values().iterator().hasNext()){
						if()
						}
						//for(int i = 0; i<=4; i++){*/
						//}
				});

				//if counter == num of detectives -> mrx is winner
				if (ref.counter == GSFdetectives.size()){winner = ImmutableSet.of(mrX.piece());}

				//else if(){}
				//if user-defined number of rounds has passed -> mrx is winner
				//if detective/police finish a move on same location/station as mrX -> detective is winner

				return winner;
			}

			@Nonnull
			@Override
			public ImmutableSet<Move> getAvailableMoves() {
				/**
				 * @return the current available moves of the game.
				 * This is mutually exclusive with {@link #getWinner()}
				*/
				ImmutableSet<Move> AvMoves = ImmutableSet.of();
				//return available moves
				//not this!!!  0 if there is a winner
				// 0 if no more tickets in ticketboard, (or in drawpile)
				// num of tickets a player has left, num of rounds left

				return AvMoves;
			}
		};
		return GSFgState;
		/*return an instance of GameSate, create a new gamestate given params
		 * Create an instance of the parameterised type given the parameters required for
		 * ScotlandYard game
		 *
		 * @param setup the game setup
		 * @param mrX MrX player
				* @param detectives detective players
		 * @return an instance of the parameterised type
		 */

	}

}
