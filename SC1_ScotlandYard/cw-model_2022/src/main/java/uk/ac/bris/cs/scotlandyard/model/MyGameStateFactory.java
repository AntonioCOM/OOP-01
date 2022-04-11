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

			HashSet<Move> AMoves = new HashSet<>();


			if(setup.moves.isEmpty()) throw new IllegalArgumentException("Moves is empty!");
			if(!mrX.isMrX()) throw new IllegalArgumentException("MrX created incorrectly, has to be black and can't be a detective");
			for(int i = 0; i<this.detectives.size(); i++) {
				if(this.detectives.get(i).isMrX()){throw new IllegalArgumentException("detective created incorrectly, swapped with MrX etc");}
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
			if(this.setup.graph.nodes().isEmpty() || this.setup.graph.edges().isEmpty()){ /*The graph with no vertices and no edges is sometimes called the null graph or empty graph,Options:
				//1. check if .nodes or .edges is empty
				//2. attempt traverse and check, ie has next
				//3.create empty and compare
				//method hasCycles doesn't work, can have 0 cycles and not be empty?*/ throw new IllegalArgumentException("Graph can't be empty!");}

			System.out.println("constructor remaining:" + this.remaining);
			System.out.println("constructor moves:" + this.AvMoves);
			System.out.println("constructor winner:" + this.winner);

			for (Piece p : this.remaining) {
				AMoves.addAll(makeSingleMoves(this.setup, this.detectives, PieceGetPlayer(p).get(), PieceGetPlayer(p).get().location()));
				if (this.log.size() + 2 <= this.setup.moves.size()) { // check if enough rounds left to make a double move
					AMoves.addAll(makeDoubleMoves(this.setup, this.detectives, PieceGetPlayer(p).get(), PieceGetPlayer(p).get().location()));
				}
			}
			this.AvMoves = ImmutableSet.copyOf(AMoves);
			this.winner = determineWinner();

			//solution: if detectives can't move, remaining must change to mrx

			System.out.println("constructor remaining:1 " + this.remaining);
			System.out.println("constructor moves:1 " + this.AvMoves);
			System.out.println("constructor winner:1 " + this.winner);
			System.out.println("(constructor)mrX location: " + mrX.location());
			for(Player p: detectives){System.out.println("detectives location: "+ p.location());}

		}

		private ImmutableSet<Piece> determineWinner(){
			this.winner = ImmutableSet.of(); //testWinningPlayerIsEmptyInitially
			ArrayList<Piece> dP = new ArrayList<>();
			HashSet<Move> XM = new HashSet<>();

			var ref = new Object() {
				int counter = 0;
			};

			XM.addAll(makeSingleMoves(this.setup, this.detectives, this.mrX, this.mrX.location() ));
			if(this.log.size() +2 <= this.setup.moves.size()){
				XM.addAll(makeDoubleMoves(this.setup, this.detectives, this.mrX, this.mrX.location()));
			}

			for(Player p : detectives){
				dP.add(p.piece());
				if(p.tickets().values().stream().allMatch(x -> x == 0)){ ref.counter ++;}
				if(p.location() == this.mrX.location()){ ref.counter = -99999;} // mrx has been caught
			}
			if(ref.counter < 0){this.winner = ImmutableSet.copyOf(dP); return winner;} // mrx has been caught detectives win

			if(XM.isEmpty()){ // mrX can't move, detectives win
				this.winner = ImmutableSet.copyOf(dP);
				return winner;
			}

			if ( ref.counter == this.detectives.size() || this.log.size() == setup.moves.size() ){
				this.winner = ImmutableSet.of(this.mrX.piece());
				return winner;
			}

			return this.winner;
		}

		private Optional<Player> PieceGetPlayer(Piece piecee){
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
		public ImmutableList<LogEntry> getMrXTravelLog() { return ImmutableList.copyOf(log);} //return log;

		/**
		 * @return the winner of this game; empty if the game has no winners yet
		 * This is mutually exclusive with {@link #getAvailableMoves()}
		 */
		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			/*winner = ImmutableSet.of(); //testWinningPlayerIsEmptyInitially
			ArrayList<Piece> dP = new ArrayList<>();
			HashSet<Move> XM = new HashSet<>();
			var ref = new Object() {
				int counter = 0;
			};

			XM.addAll(makeSingleMoves(this.setup, this.detectives, mrX, mrX.location() ));
			if(this.log.size() +2 <= this.setup.moves.size()){
				XM.addAll(makeDoubleMoves(this.setup, this.detectives, mrX, mrX.location()));
			}

			detectives.forEach(p ->{
				dP.add(p.piece());
				if (p.tickets().values().stream().count() == 0){ ref.counter = ref.counter + 1;}
				if(p.location() == mrX.location()){ winner = ImmutableSet.copyOf(dP);} // mrx has been caught

			});
			if(XM.isEmpty()){ // mrX can't move, detective wins
				winner = ImmutableSet.copyOf(dP);
			}

			if ( (ref.counter == detectives.size() && winner.isEmpty()) || (log.size() == setup.moves.size() && ref.counter == detectives.size()) ){
				winner = ImmutableSet.of(mrX.piece());
			}
			*/
			System.out.println("getwinner: " + this.winner);
			return this.winner;
		}

		/**
		 * @return the current available moves of the game.
		 * This is mutually exclusive with {@link #getWinner()}
		 */
		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			if(!winner.isEmpty()){this.AvMoves = ImmutableSet.of();}
			System.out.println("getAVMoves: "+this.AvMoves);
			return this.AvMoves;
		}

		private static Set<SingleMove> makeSingleMoves
				(GameSetup setup, List<Player> detectives, Player player, int source){
			HashSet<SingleMove> sMoves = new HashSet<>(); //type inference
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

			System.out.println("advance remaining:" + this.remaining);
			System.out.println("advance moves:" + this.AvMoves);
			System.out.println("advance winner:" + this.winner);


			GameState NewGS = move.accept(new Visitor<GameState>(){
				int dm = 0;
				@Override public GameState visit(SingleMove singleMove){
					if(singleMove.commencedBy().isMrX()){
						ArrayList<LogEntry> LE = new ArrayList<>(log);
						if(setup.moves.get(LE.size())) {
							LE.add(LogEntry.reveal(singleMove.ticket, singleMove.destination) );
							log = ImmutableList.copyOf(LE);
						}
						else if(!setup.moves.get(LE.size())){
							LE.add(LogEntry.hidden(singleMove.ticket));
							log = ImmutableList.copyOf(LE);
						}
						mrX = mrX.at(singleMove.destination);
						if(dm == 0) { // if it is a double move, first move skip this
							ArrayList<Piece> DPieces = new ArrayList<>();
							for (Player p : detectives) {
								DPieces.add(p.piece());
							}
							remaining = ImmutableSet.copyOf(DPieces); // after mrX has moved change remaing to detectives
						}
						mrX = mrX.use(singleMove.ticket);
						return new MyGameState(getSetup(), remaining, log, mrX, detectives);
					}

					else if(singleMove.commencedBy().isDetective()){
						Player p = PieceGetPlayer(singleMove.commencedBy()).get();
						int a = 0;
						for(int i=0; i<detectives.size(); i++){
							if(detectives.get(i).piece() == singleMove.commencedBy()){
								a = i;
								break;
							}
						}
						p = p.at(singleMove.destination);
						p = p.use(singleMove.ticket);
						mrX = mrX.give(singleMove.ticket);
						ArrayList <Player> dAL = new ArrayList<>(detectives);
						dAL.add(a,p);
						dAL.remove(a+1);
						detectives = ImmutableList.copyOf(dAL);
						remaining = ImmutableSet.copyOf(remaining.stream().filter(x -> x !=singleMove.commencedBy()).toList());
						ArrayList<Integer> dt = new ArrayList<>();

						if(remaining.isEmpty() || remaining.stream().allMatch(x-> PieceGetPlayer(x).get().tickets().values().stream().allMatch(y -> y==0)  ) ){ // checks if detectives can still move, and switches remaining to mrX if requried
							remaining = ImmutableSet.of(mrX.piece());
						}
						return new MyGameState(getSetup(), remaining, log, mrX, detectives);

					}
					return null;
				}
				@Override public GameState visit(DoubleMove doubleMove){

					SingleMove FSM = new SingleMove(doubleMove.commencedBy(), doubleMove.source(), doubleMove.ticket1, doubleMove.destination1);
					SingleMove SSM = new SingleMove(doubleMove.commencedBy(), doubleMove.destination1, doubleMove.ticket2, doubleMove.destination2);
					mrX = mrX.use(Ticket.DOUBLE);
					/*ArrayList<LogEntry> LE = new ArrayList<>(log);
					if(setup.moves.get(log.size())) {
						LE.add(LogEntry.reveal(FSM.ticket, mrX.location()) );
						log = ImmutableList.copyOf(LE);
					}
					else if(!setup.moves.get(log.size())){
						LE.add(LogEntry.hidden(FSM.ticket));
						log = ImmutableList.copyOf(LE);
					}
					mrX.use(FSM.ticket);
					mrX = mrX.at(FSM.destination);

					return  visit(SSM); */

					//return new MyGameState(getSetup(), remaining, log, mrX, detectives);

					dm = 1;
					GameState xd;
					xd = visit(FSM);
					dm = 0;
					xd = visit(SSM);
					return xd;
				}

			});
			if(NewGS == null){System.out.println("returned state is null");}
			System.out.println("advance remaining:2 " + this.remaining);
			System.out.println("advance moves:2 " + this.AvMoves);
			System.out.println("advance winner:2 " + this.winner);
			return NewGS;
		}
	}
}