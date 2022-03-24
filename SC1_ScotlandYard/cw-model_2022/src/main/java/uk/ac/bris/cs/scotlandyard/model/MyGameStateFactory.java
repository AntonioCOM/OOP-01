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
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.frequency;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState>{

	/**
	 * Create an instance of the parameterised type given the parameters required for
	 * ScotlandYard game
	 *
	 * @param setup the game setup
	 * @param mrX MrX player
	 * @param detectives detective players
	 * @return an instance of the parameterised type
	 */
	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		// TODO
		//throw new RuntimeException("Implement me!");
		/*GameSetup GSFSetup = setup;
		GameState f = null;
		Player GSFmrX = mrX;
		ImmutableList<Player> GSFdetectives = detectives;
		final ImmutableSet<LogEntry> IS_GSFlog;*/
		return new MyGameState(setup, ImmutableSet.of(mrX.piece()/*.MRX*/), ImmutableList.of(), mrX, detectives);

	}

	private final class MyGameState implements GameState{
		private GameSetup setup; //to return it, as well as have access to the game graph and Mr X reveal moves
		private ImmutableSet<Piece> remaining; //to hold which pieces (are still in the game) can still move in current round,(just MrX at the starting round)
		private ImmutableList<LogEntry> log; // to hold the travel log and count the moves Mr has taken, (empty at the starting round)
		private Player mrX; //
		private List<Player> detectives; // list of detectives in the game
		private ImmutableSet<Move> moves; //to hold the currently possible/available moves
		private ImmutableSet<Piece> winner; // to hold the current winner(s)


		// constructor
		private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining,
				final ImmutableList<LogEntry> log,
				final Player mrX,
				final List<Player> detectives){
			this.setup = checkNotNull(setup,"setup can't be null"); // from import static com.google.common.base.Preconditions.checkNotNull;
			this.remaining = checkNotNull(remaining,"remaining can't be null"); // throws NullPointerException - if reference is null
			this.log = checkNotNull(log,"Travel log(List<LogEntry>) can't be null");
			this.mrX = checkNotNull(mrX,"mrX(player) can't be null");
			this.detectives = checkNotNull(detectives,"detectives(List<Player>) can't be null");

			if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty!");
			if(!mrX.isMrX()) throw new IllegalArgumentException("MrX created incorrectly, has to be black and can't be a detective");
			for(int i = 0; i<detectives.size(); i++) {
				if(detectives.get(i).isMrX()){throw new IllegalArgumentException("detective created incorrectly, swapped with MrX etc");}
				//if(detectives.get(i).piece() /*.equals()????*/ == detectives.get(counter).piece() || detectives.get(i).location() == detectives.get(counter).location()){throw new IllegalArgumentException("duplicate detective or same location");}
				int fd = frequency(detectives, detectives.get(i));
				if(fd != 1 ){throw new IllegalArgumentException("detectives created incorrectly");}
				// detectives can't have secret or double tickets
				if(detectives.get(i).has(Ticket.SECRET) || detectives.get(i).has(Ticket.DOUBLE)){throw new IllegalArgumentException("detectives have illegal tickets");}
				for(int j = i+1; j<detectives.size();j++){
					if(detectives.get(j).location() == detectives.get(i).location()){throw new IllegalArgumentException("detectives locations overlap");}
				}
			}
		}

		/**
		 * @return the current game setup
		 */
		@Nonnull
		@Override
		public GameSetup getSetup() {return this.setup;}

		/**
		 * @return all players in the game /// ?currently in the game?
		 */
		@Nonnull
		@Override
		public ImmutableSet<Piece> getPlayers() {
			/* just return remaining, add  to check if players are still in the game to not make remaining wrong
			List<Piece> DP = new ArrayList<>();
			for(int i = 0; i <detectives.size(); i++){DP.add(detectives.get(i).piece());}
			remaining =
					ImmutableSet.<Piece>builder()
							.add(mrX.piece())
							.addAll(DP)
							.build();
			*/
			List<Piece> DP = new ArrayList<>();
			for(int i = 0; i <detectives.size(); i++){DP.add(detectives.get(i).piece());}

			/*static*/
			final ImmutableSet<Piece> GSFplayers =
					ImmutableSet.<Piece>builder()
							.add(mrX.piece())
							//.addAll( GSFdetectives.iterator() GSFdetectives.piece())
							//GSFdetectives.forEach((p) -> (List<Piece> DP =  p.piece()));
							.addAll(DP)
							.build();
			return GSFplayers;
			//return remaining;
		}

		/**
		 * @param detective the detective
		 * @return the location of the given detective; empty if the detective is not part of the game
		 */
		 // For all detectives, if Detective#piece == detective, then return the location in an Optional.of();
		 // otherwise, return Optional.empty();
		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
			var ref = new Object() {
				Player id = null;
			};
			detectives.forEach((p) -> {
				if(p.piece() == detective ){ref.id = p;} //&&!mrX	???
			});
			if(ref.id == null){return Optional.empty();}
			return Optional.ofNullable(ref.id.location()); // ref.id can't be null,due to .location. otherwise ofNullable is sufficient
		}

		/**
		 * @param piece the player piece
		 * @return the ticket board of the given player; empty if the player is not part of the game
		 */
		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			var ref = new Object() {
				Player id = null;
			};

			if(piece.isMrX()){ref.id = mrX;/*System.out.println("Piece provided is mrx:"+ ref.id);*/}

			else if (piece.isDetective()) {
				detectives.forEach((p) -> {
					//id = p.piece();
					if (p.piece() == piece) {ref.id = p;/*System.out.println("Piece provided is:"+ p);*/}
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

		/**
		 * @return MrX's travel log as a list of {@link LogEntry}s.
		 */
		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {
			/*private static*/ ArrayList<LogEntry> GSFlog = new ArrayList<>();

			//LogEntry a = LogEntry.hidden();
			//LogEntry GSFlog = new LogEntry.hidden((mrX.tickets()).getOrDefault(null , ScotlandYard.Ticket()));
			//ImmutableMap<ScotlandYard.Ticket, Integer> tickets
			//mrX.tickets());
			//(@Nonnull
			//ScotlandYard.Ticket ticket)
			//LogEntry GSFlog = new LogEntry.hidden();

			// use builder etc???????

			return log;
			//return ImmutableList.copyOf(GSFlog);
		}

		/**
		 * @return the winner of this game; empty if the game has no winners yet
		 * This is mutually exclusive with {@link #getAvailableMoves()}
		 */
		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			winner = ImmutableSet.of(); //testWinningPlayerIsEmptyInitially
			var ref = new Object() {
				int counter = 0;
			};
			detectives.forEach(p ->{
				if (p.tickets().values().stream()/*.filter(x -> x>0)*/.count() == 0){
					ref.counter = ref.counter + 1;}

					/*while(p.tickets().values().iterator().hasNext()){
						if()
						}
						//for(int i = 0; i<=4; i++){*/
				//}
			});

			//if counter == num of detectives -> mrx is winner
			if (ref.counter == detectives.size()){winner = ImmutableSet.of(mrX.piece()); System.out.println("mrX wins, no more detective");}

			//else if(){}
			//if user-defined number of rounds has passed -> mrx is winner
			//if detective/police finish a move on same location/station as mrX -> detective is winner

			return winner;
		}

		/**
		 * @return the current available moves of the game.
		 * This is mutually exclusive with {@link #getWinner()}
		 */
		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			ImmutableSet<Move> AvMoves = ImmutableSet.of();
			//return available moves
			//not this!!!  0 if there is a winner
			// 0 if no more tickets in ticketboard, (or in drawpile)
			// num of tickets a player has left, num of rounds left

			return AvMoves;
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

}
