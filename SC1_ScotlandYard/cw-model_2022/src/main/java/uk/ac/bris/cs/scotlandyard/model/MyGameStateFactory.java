package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState>{

	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		// TODO
		//throw new RuntimeException("Implement me!");

		GameSetup GSFSetup = setup;
		Player GSFmrX = mrX;
		ImmutableList<Player> GSFdetectives = detectives;

		GameState GSFgState = new GameState() {
			@Nonnull
			@Override
			public GameState advance(Move move) {
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
				//return null;
				List<Piece> DP = new ArrayList<>();
				for(int i = 0; i <GSFdetectives.size(); i++){DP.add(GSFdetectives.get(i).piece());}

				/*static*/
				final ImmutableSet<Piece> GSFplayers =
						ImmutableSet.<Piece>builder()
								.add(GSFmrX.piece())
								//.addAll( GSFdetectives.iterator() GSFdetectives.piece())
								//GSFdetectives.forEach( (p) -> (List<Piece> DP =  p.piece()));
								.addAll(DP)
								.build();

				return GSFplayers;

			}

			@Nonnull
			@Override
			public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
				return Optional.empty();
			}

			@Nonnull
			@Override
			public Optional<TicketBoard> getPlayerTickets(Piece piece) {
				return Optional.empty();
			}

			@Nonnull
			@Override
			public ImmutableList<LogEntry> getMrXTravelLog() {
				return null;
			}

			@Nonnull
			@Override
			public ImmutableSet<Piece> getWinner() {
				return null;
			}

			@Nonnull
			@Override
			public ImmutableSet<Move> getAvailableMoves() {
				return null;
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
