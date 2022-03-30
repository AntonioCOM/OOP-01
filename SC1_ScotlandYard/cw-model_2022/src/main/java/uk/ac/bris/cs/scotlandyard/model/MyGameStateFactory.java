package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
import static com.google.common.base.Preconditions.checkNotNull;
//import static com.google.common.graph.Graphs.*;
import static com.google.common.graph.Graphs.hasCycle;
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
		private ImmutableSet<Piece> remaining; //to hold which pieces  can still move in current round,(just MrX at the starting round)
		private ImmutableList<LogEntry> log; // to hold the travel log and count the moves Mr has taken, (empty at the starting round)
		private Player mrX; //
		private List<Player> detectives; // list of detectives in the game
		private ImmutableSet<Move> AvMoves; //to hold the currently possible/available moves !!!changed moves to AvMoves!!!!
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

			//this.remaining = ImmutableSet.of(this.mrX.piece());

			if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty!");
			if(!mrX.isMrX()) throw new IllegalArgumentException("MrX created incorrectly, has to be black and can't be a detective");
			HashSet<Move> AMoves = new HashSet<>();
			for(int i = 0; i<this.detectives.size(); i++) {
				//AMoves.addAll(makeSingleMoves(setup, this.detectives, this.detectives.get(i), this.detectives.get(i).location()));
				//AMoves.addAll(makeDoubleMoves(setup, this.detectives, this.detectives.get(i), this.detectives.get(i).location()));
				if(this.detectives.get(i).isMrX()){throw new IllegalArgumentException("detective created incorrectly, swapped with MrX etc");}
				//if(detectives.get(i).piece() /*.equals()????*/ == detectives.get(counter).piece() || detectives.get(i).location() == detectives.get(counter).location()){throw new IllegalArgumentException("duplicate detective or same location");}
				int fd = frequency(this.detectives, this.detectives.get(i));
				if(fd != 1 ){throw new IllegalArgumentException("detectives created incorrectly");}
				// detectives can't have secret or double tickets
				if(this.detectives.get(i).has(Ticket.SECRET) || detectives.get(i).has(Ticket.DOUBLE)){throw new IllegalArgumentException("detectives have illegal tickets");}
				for(int j = i+1; j<this.detectives.size();j++){
					if(this.detectives.get(j).location() == this.detectives.get(i).location()){
						throw new IllegalArgumentException("detectives locations overlap");
					}
				}
			}
			//AMoves.addAll(makeSingleMoves(setup, this.detectives, mrX, mrX.location()));
			//AMoves.addAll(makeDoubleMoves(setup, this.detectives, mrX, mrX.location()));
			//this.AvMoves = ImmutableSet.copyOf(AMoves);
			if(this.setup.graph.nodes().isEmpty() || this.setup.graph.edges().isEmpty()){ /*The graph with no vertices and no edges is sometimes called the null graph or empty graph,Options:
				//1. check if .nodes or .edges is empty
				//2. attempt traverse and check, ie has next
				//3.create empty and compare
				//method hasCycles doesn't work, can have 0 cycles and not be empty?*/ throw new IllegalArgumentException("Graph can't be empty!");}
			for(Piece p : this.remaining ){
				AMoves.addAll(makeSingleMoves(setup, this.detectives, PieceGetPlayer(p).get(), PieceGetPlayer(p).get().location() ));
				AMoves.addAll(makeDoubleMoves(setup, this.detectives, PieceGetPlayer(p).get(), PieceGetPlayer(p).get().location()));
			}
			this.AvMoves = ImmutableSet.copyOf(AMoves);
			// update set remaining, then change uses of list players for remaining
		}

		public Optional<Player> PieceGetPlayer(Piece piecee){
			var ref = new Object() {
				Player id = null;
			};

			if(piecee.isMrX()) {ref.id = this.mrX; }

			else if (piecee.isDetective()) {
				this.detectives.forEach((p) -> {
					if (p.piece() == piecee) {ref.id = p;}
				});
			}
			return Optional.ofNullable(ref.id);
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
			for(int i = 0; i <this.detectives.size(); i++){DP.add(this.detectives.get(i).piece());}

			/*static*/
			final ImmutableSet<Piece> GSFplayers =
					ImmutableSet.<Piece>builder()
							.add(this.mrX.piece())
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
			/*var ref = new Object() {
				Player id = null;
			};

			detectives.forEach((p) -> {
				if(p.piece() == detective ){ref.id = p;} //&&!mrX	???
			});
			*/
			Player id = PieceGetPlayer(detective).orElse(null);

			if(id == null){return Optional.empty();}
			return Optional.ofNullable(id.location()); // ref.id can't be null,due to .location. otherwise ofNullable is sufficient
		}

		/**
		 * @param piece the player piece
		 * @return the ticket board of the given player; empty if the player is not part of the game
		 */
		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			/*var ref = new Object() {
				Player id = null;
			};

			if(piece.isMrX()){ref.id = mrX;}

			else if (piece.isDetective()) {
				detectives.forEach((p) -> {
					//id = p.piece();
					if (p.piece() == piece) {ref.id = p;}
				});
			}*/
			Player id = PieceGetPlayer(piece).orElse(null);
			if(id == null){return Optional.empty();}

			TicketBoard GSFTB = new TicketBoard() {
				@Override
				public int getCount(@Nonnull ScotlandYard.Ticket ticket) {
					/**
					 * @param ticket the ticket to check count for
					 * @return the amount of ticket,that player has, always &gt;>= 0
					 */
					return id.tickets().get(ticket);
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
			if (ref.counter == detectives.size()){
				winner = ImmutableSet.of(mrX.piece()); System.out.println("mrX wins, no more detective");
			}

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
			// get avmoves from player in remaining, do this in constructor
			return AvMoves; /*HashSet<Move> AMoves = new HashSet<>();
			//return possible moves that can be done, maybe use remaining player rather than all players passed as parameter
			for(Player p : detectives){
				AMoves.addAll(makeSingleMoves(setup, detectives, p, p.location()));
				AMoves.addAll(makeDoubleMoves(setup, detectives, p, p.location()));
			}
			AMoves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));
			AMoves.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location()));

			moves = ImmutableSet.copyOf(AMoves);
			System.out.println(moves);
			return ImmutableSet.copyOf(AMoves); // moves*/
		}

		private static Set<SingleMove> makeSingleMoves
				(GameSetup setup, List<Player> detectives, Player player, int source){
			HashSet<SingleMove> sMoves = new HashSet<>(); //type inference
			// player is the player that is making the move
			// TODO create an empty collection of some sort, say, HashSet, to store all the SingleMove we generate

			for(int destination1 : setup.graph.adjacentNodes(source)) { //int source = node, ie current piece position
				// TODO find out if destination is occupied by a detective
				if(detectives.stream().noneMatch(x->x.location() == destination1)) { ///List.stream(detectives). ->
					for (Transport t1 : setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of())) {
						// TODO find out if the player has the required tickets
						//  if it does, construct a SingleMove and add it the collection of moves to return
						//&& player.has(setup.graph.edgeValue(source,destination).isPresent()
						//sMoves.add(new SingleMove(player.piece(), source, player));
						if (player.has(t1.requiredTicket())) {
							sMoves.add(new SingleMove(player.piece(), source, t1.requiredTicket(), destination1));
						}
						/* double move calculation
						if(player.has(Ticket.DOUBLE) && (player.has(t1.requiredTicket()) || player.has(Ticket.SECRET))){ // double move
							for(int destination2 : setup.graph.adjacentNodes(destination1)) {
								// TODO find out if destination is occupied by a detective
								if(detectives.stream().noneMatch(x->x.location() == destination2)){
									for (Transport t2 : setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of())) {
										// TODO find out if the player has the required tickets
										//  if it does, construct a SingleMove and add it the collection of moves to return
										if (player.has(t2.requiredTicket())) {
											if((t1.requiredTicket() == t2.requiredTicket()) && player.hasAtLeast(t1.requiredTicket(), 2) ){ // use .equals()????
												sMoves.add(new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
											}
											else if(t1.requiredTicket() != t2.requiredTicket()){
												sMoves.add(new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
											}
										}

										// Use secret ticket for first move

									}
									if (player.has(Ticket.SECRET)
											&& !((setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of(Transport.FERRY))).contains(Transport.FERRY))) {  // could use stream.none match, also set default value to ferry just in case
										sMoves.add(new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, Ticket.SECRET, destination2));
									}
									if(player.hasAtLeast(Ticket.SECRET,2) && !((setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of(Transport.FERRY))).contains(Transport.FERRY)) && !((setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of(Transport.FERRY))).contains(Transport.FERRY))){
										sMoves.add(new DoubleMove(player.piece(), source, Ticket.SECRET, destination1, Ticket.SECRET, destination2));
									}

									// case in which first ticket used is secret and second is the required ticket
									//if (player.has(Ticket.SECRET)
									//		&& !((setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of(Transport.FERRY))).contains(Transport.FERRY))) {  // could use stream.none match, also set default value to ferry just in case
									//	sMoves.add(new DoubleMove(player.piece(), source, Ticket.SECRET, destination1, t2.requriedTicket, destination2));
									//}
								}
							}
						}*/
					}

					// TODO consider the rules of secret moves here
					//  add moves to the destination via a secret ticket if there are any left with the player
					if (player.has(Ticket.SECRET)
							&& !((setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of(Transport.FERRY))).contains(Transport.FERRY))) {  // could use stream.none match, also set default value to ferry just in case
						sMoves.add(new SingleMove(player.piece(), source, Ticket.SECRET, destination1));
					}
				}
			}
			// TODO return the collection of moves
			return sMoves;
		}

		private static Set<DoubleMove> makeDoubleMoves
				(GameSetup setup, List<Player> detectives, Player player, int source){
			HashSet<DoubleMove> dMoves = new HashSet<>();
			ArrayList<SingleMove> firstM = new ArrayList<>(makeSingleMoves(setup, detectives, player, source));
			HashSet<SingleMove> secondM;
			// check if more than 2 rounds left in the game
			if(/*player.isMrX() &&*/ player.has(Ticket.DOUBLE)) {
				for (SingleMove firstSM : firstM){//(int i = 0; i < firstM.size(); i++) { // iterate through first single moves
					secondM = new HashSet<>(makeSingleMoves(setup, detectives, player, firstSM.destination)); // calculate possible second moves
					for (SingleMove secondSM : secondM){//(int j = 0; j < secondM.size(); j++) {// iterate through second single moves
						// check if player has requried ticket to complete secondSM, given the completion of fistSM
						if(secondSM.ticket == firstSM.ticket && player.hasAtLeast(secondSM.ticket, 2)){ // check if p
							dMoves.add(new DoubleMove(player.piece(), source, firstSM.ticket, firstSM.destination, secondSM.ticket, secondSM.destination));
						}
						else if(secondSM.ticket != firstSM.ticket){
							dMoves.add(new DoubleMove(player.piece(), source, firstSM.ticket, firstSM.destination, secondSM.ticket, secondSM.destination));
						}
					}
				}
			}
			/*
			HashSet<DoubleMove> dMoves = new HashSet<>(); //type inference
			HashSet<SingleMove> nd_move = new HashSet<>(); //type inference
			List<Move.SingleMove> sm= new ArrayList<>(makeSingleMoves(setup, detectives, player, source));

			sm = makeSingleMoves(setup, detectives, player, source).stream()
					.filter(x -> (x.commencedBy().isMrX() && player.has(Ticket.DOUBLE)))
					.toList();

			for(int i =0; i<sm.size(); i++){ // loop through possible single moves
				nd_move.addAll(makeSingleMoves(setup, detectives, player, sm.get(i).destination));

				for(int destination2 : setup.graph.adjacentNodes(sm.get(i).destination)){ // loop through adj nodes to 1 destination
					if(detectives.stream().noneMatch(x->x.location() == destination2)){
						for (Transport t2 : setup.graph.edgeValueOrDefault(sm.get(i).destination, destination2, ImmutableSet.of())) {

						}
					}
				}
			}




			/*if(player.has(Ticket.DOUBLE) && (player.has(t1.requiredTicket()) || player.has(Ticket.SECRET))){ // double move
				for(int destination2 : setup.graph.adjacentNodes(destination1)) {
					// TODO find out if destination is occupied by a detective
					if(detectives.stream().noneMatch(x->x.location() == destination2)){
						for (Transport t2 : setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of())) {
							// TODO find out if the player has the required tickets
							//  if it does, construct a SingleMove and add it the collection of moves to return
							if (player.has(t2.requiredTicket())) {
								if((t1.requiredTicket() == t2.requiredTicket()) && player.hasAtLeast(t1.requiredTicket(), 2) ){ // use .equals()????
									sMoves.add(new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
								}
								else if(t1.requiredTicket() != t2.requiredTicket()){
									sMoves.add(new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2));
								}
							}
							// Use secret ticket for first move
							// use 2 secret tickets for double move
						}
						if (player.has(Ticket.SECRET)
								&& !((setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of(Transport.FERRY))).contains(Transport.FERRY))) {  // could use stream.none match, also set default value to ferry just in case
							sMoves.add(new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, Ticket.SECRET, destination2));
						}
					}
				}
			}*/

			return dMoves;
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
			if(!AvMoves.contains(move)) throw new IllegalArgumentException("Illegal move: "+move);


			/*
			// do I really need to use the visitor pattern

			if(move.commencedBy().isMrX()){
				if(move.toString().contains("x2")){ // double move

				}
				else{

				}
			}
			else if (move.commencedBy().isDetective()){


			}*/ // just use visitor pattern. other way would require reflection pattern etc and will be more complicated
			return null;
		}
	}

}