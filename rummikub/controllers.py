import time

from collections import Counter
from emoji import emojize
from rummikub.objects import (
    MAX_NUMBER_OF_TILES,
    Player,
    Pool,
    Table,
    TILE_NUMBERS,
    Tile,
    TileColor
    )
from textual.app import App
from typing import List, Dict

class RummikubController:
    """The main rummikub game model.
    """
    def __init__(self,
                 view:App,
                 total_players: int,
                 human_players: int,
                 seed: int | None = None,
                 options: Dict[str, any]| None = None):
        self.view: App = view
        self.current_turn: int = -1
        self.current_player:Player = None
        self.seed = seed

        ##############################################################################
        # MODEL
        ##############################################################################
        self.pool:Pool = Pool()

        self.table:Table = Table()

        self.options = options if options else {
            'strategy': 'highest-value', # 'highest-value', 'longest-run', 'all-in', 'human'
            'sort-table': True,
            'stop-at-turn': -1, # -1 means no stop
        }

        self.players: List[Player] = []

        for i in range(human_players):
            self.players.append(Player(emojize(f":man: #{i+1}"), True))
        for i in range(total_players - human_players):
            # fake_name = Faker().name()
            self.players.append(Player(emojize(f":robot: #{i+1}"), False))


    def initialize(self):
        """Initialize the game model."""
        self.pool.initialize()
        self.pool.shuffle(self.seed)

        for _ in range(MAX_NUMBER_OF_TILES):
            for player in self.players:
                player.draw_tile(self.pool)

        self.update_pool()
        self.update_table()
        self.next_player()

    def update_pool(self):
        """Trigger an update of the pool view from the model."""
        self.view.update_pool(self.pool)

    def update_player(self):
        """Trigger an update of the player view from the model."""
        self.view.update_player(self.current_player)

    def update_table(self):
        """Trigger an update of the table view from the model."""
        self.view.update_table(self.table)

    def update_status(self, message: str):
        """Trigger an update of the status view from the model."""
        self.view.set_status(message)

    def play_turn(self):
        """Advance to the next player's turn."""
        if self.options['stop-at-turn'] > -1 and self.current_turn == self.options['stop-at-turn'] - 1:
            self.log(f"Turn #{self.current_turn + 1} reached ... you can break in the debugger")
            pass

        # Play the turn for the current player
        self.current_player = self.players[self.current_turn % len(self.players)]

        # Let's play the turn
        valid_sets = self._find_sets(self.current_player.hand)
        if not self.current_player.has_played_initial:
            # Initial draw: only accept sets with a total point value superior to 30
            # BUG: This is not working properly with overlapping tiles
            valid_sets = valid_sets if self._sum_melds(valid_sets) >= 30 else []

        if valid_sets:
            self._prompt_and_play_turn(valid_sets)
        elif self.pool:
            self.update_status(emojize(f":warning:  No move for player {self.current_player.name}, drawing tile from pool."))
            self.current_player.draw_tile(self.pool)
        else:
            self.update_status(emojize(":warning:  No valid moves and the pool is empty."))

        self.update_pool()
        self.update_table()

        self.next_player()

    def next_player(self):
        """Advance to the next player without playing a turn."""
        self.current_turn += 1
        self.current_player = self.players[self.current_turn % len(self.players)]
        self.update_player()

    #
    # Properties
    #
    @property
    def strategy(self):
        """Getter for strategy"""
        return self.options['strategy']

    @strategy.setter
    def strategy(self, value):
        """Setter for strategy (with validation)"""
        if not value:
            raise ValueError("strategy cannot be empty")
        self.options['strategy'] = value

    #
    # Private methods
    #
    def _find_sets(self, hand: List[Tile]) -> List[List[Tile]]:
        """
        Finds all valid sets (runs and groups) that can be formed from a given list of tiles.

        A set can be either:
        - A run: a sequence of tiles with the same color and consecutive numbers.
        - A group: a set of tiles with the same number and different colors.

        Jokers are handled and can substitute any tile in a set.

        NOTE: that we do not check for overlapping tiles between sets. This is handled later.

        Args:
            hand (List[Tile]): The list of tiles to search for valid sets.

        Returns:
            List[List[Tile]]: A list of sets, where each set is a list of Tile objects representing a valid run or group.
        """
        sets = []
        jokers = [t for t in hand if t.is_joker]
        color_groups = {color: [] for color in TileColor}
        number_groups = {str(n): [] for n in TILE_NUMBERS}

        # Add each tile to a group according to its color and number
        for tile in hand:
            if not tile.is_joker:
                color_groups[tile.color].append(tile)
                number_groups[str(tile.number)].append(tile)

        # find the runs: series of identical color tiles in ascending order
        sets.extend(self._find_runs(color_groups, jokers))

        # find the group: series of identical numbered tiles with different colors
        sets.extend(self._find_groups(number_groups, jokers))

        return sets

    def _find_runs(self, color_groups: Dict[str, List[Tile]], jokers: List[Tile]) -> List[List[Tile]]:
        runs = []
        for color, color_group in color_groups.items():
            sorted_group = sorted(color_group, key=lambda t: t.number)
            numbers = [tile.number for tile in sorted_group]
            used_jokers = [False] * len(jokers)
            max_number = max(numbers, default=0)

            for start in range(1, max_number + 1):
                run = []
                current = start
                j_index = 0
                idx = 0

                while current <= 13:
                    tile_found = False
                    while idx < len(sorted_group) and sorted_group[idx].number < current:
                        idx += 1

                    if idx < len(sorted_group) and sorted_group[idx].number == current:
                        run.append(sorted_group[idx])
                        idx += 1
                        tile_found = True
                    elif j_index < len(jokers):
                        run.append(Tile(True))
                        used_jokers[j_index] = True
                        j_index += 1
                        tile_found = True
                    else:
                        break

                    if len(run) >= 3:
                        runs.append(run.copy())

                    current += 1

        return runs

    def _find_groups(self, number_groups: Dict[int, List[Tile]], jokers: List[Tile]) -> List[List[Tile]]:
        groups = []
        for number, number_group in number_groups.items():
            # Remove duplicated tiles (same color) from group
            number_group = list(set(number_group))
            colors_present = set(t.color for t in number_group)
            available_colors = [c for c in TileColor if c not in colors_present]
            needed = 3 - len(colors_present)
            if needed <= len(jokers):
                used_jokers = [Tile(True) for i in range(needed)]
                groups.append(number_group + used_jokers)
        return groups

    def _say(self, player, message):
        if player.is_human:
            print(emojize(message))

    def _ask(self, player, message) -> str:
        if player.is_human:
            return input(emojize(message))
        return None

    def _prompt_and_play_turn(self, valid_sets: List[List[Tile]]):
        selected_sets = []

        if self.current_player.is_human:
            selected_sets = self._prompt_human_move(self.current_player, valid_sets)
        elif valid_sets:
            selected_sets = self._select_best_melds(valid_sets)

        if selected_sets:
            for meld in selected_sets:
                self.current_player.remove_meld(meld)
                self.table.add_meld(meld)
                # if not player.is_human:
                #     time.sleep(1)
            self.current_player.has_played_initial = True
            self.update_status(emojize(f":clapping_hands: New meld on the table from {self.current_player.name}!"))
        else:
            self.current_player.draw_tile(self.pool)

    def _sum_melds(self, melds: List[List[Tile]]|List[Tile]) -> int:
        """Returns the sum of a list of meld

        Args:
            melds (List[List[Tile]]|List[Tile]): The list of meld (or the meld itself) to sum

        Returns:
            int: the total value
        """
        if len(melds) > 0 and isinstance(melds[0], List):
            return sum(tile.number for meld in melds for tile in meld if not tile.is_joker)
        return sum(tile.number for tile in melds if not tile.is_joker)

    def _select_best_melds(self, melds: List[List[Tile]]) -> List[List[Tile]]:
        # Apply non-overlapping filter to all strategies
        if self.options['strategy'] in ('highest-value', 'longest-run', 'all-in'):
            key_func = self._sum_melds if self.options['strategy'] == 'highest-value' else len
            sorted_melds = sorted(melds, key=key_func, reverse=True)

            selected_melds = []
            used_tile_counts = Counter()

            for meld in sorted_melds:
                meld_tile_counts = Counter(meld)
                if all(used_tile_counts[tile] + meld_tile_counts[tile] <= 1 for tile in meld_tile_counts):
                    selected_melds.append(meld)
                    used_tile_counts.update(meld_tile_counts)

            return selected_melds

        return melds

    def _prompt_human_move(self, player: Player, sets: List[List[Tile]]) -> List[List[Tile]]:
        """
        Prompts a human player to select melds (sets of tiles) to play from a list of available options.

        Args:
            player (Player): The current player making the move.
            sets (List[List[Tile]]): A list of possible melds (each meld is a list of Tile objects) that the player can play.

        Returns:
            List[List[Tile]]: The list of melds selected by the player to play. Returns an empty list if the player skips or provides invalid input.

        Behavior:
            - Displays all available melds with their indices and total values.
            - Prompts the player to enter comma-separated indices of melds to play.
            - Validates the selection to ensure melds are not reused and meet game requirements.
            - Returns the selected melds if valid, otherwise prompts again or returns an empty list on invalid input.
        """
        selected_melds = []

        while True:
            self._say(player, ":light_bulb: To modify an existing meld from the table, use its indice (i.e. 't0', 't1', etc.)")
            self._say(player, ":light_bulb: Available melds to play from your hand:")
            for idx, meld in enumerate(sets):
                self._say(player, f"h{idx}: {' '.join(str(tile) for tile in meld)} (value: {self._sum_melds([meld])})")

            selection = self._ask(player, ":backhand_index_pointing_right: Enter comma-separated indices of melds to play (Enter to skip): ").strip()
            if not selection:
                return []

            try:
                indices = [i.strip() for i in selection.split(',') if i.strip().isdigit()]
                selected_melds = self._validate_meld_selection(player, sets, indices)

                if selected_melds:
                    return selected_melds
                else:
                    self._say(player, ":cross_mark: Invalid selection. Tiles may be reused or total value may be too low.")
            except Exception:
                self._say(player, ":cross_mark: Invalid input format.")
                return []

    def _validate_meld_selection(self, player: Player, sets: List[List[Tile]], selected_indices: List[int]) -> List[List[Tile]]:
        """Returns whether the list of selected meld is valid according to the Player's tiles

        If a meld selection is overusing a specific Player's Tile, then the
        selection is invalid, and valid otherwise.

        Args:
            player (Player): The player to check the selection against.
            sets (List[List[Tile]]): The set of valid melds.
            selected_indices (List[int]): The list of selected meld.

        Returns:
            List[List[Tile]]: The valid list of meld resulting from the list of selected indices.
        """
        # Count the number of times each tile is held by the player.
        # Count the number of times each tile is used across the selected melds.
        # Reject the selection if any tile is overused.
        selected_melds = [sets[i] for i in selected_indices if 0 <= i < len(sets)]
        tile_counts = Counter(player.tiles)
        used_tile_counts = Counter(tile for meld in selected_melds for tile in meld)

        for tile, used_count in used_tile_counts.items():
            if used_count > tile_counts[tile]:
                return []  # Invalid: overlapping tiles

        if not player.has_played_initial and self._sum_melds(selected_melds) < 30:
            return []  # Invalid: not enough points for initial move

        return selected_melds

    def _is_valid_meld(self, meld: List[Tile]) -> bool:
        if len(meld) < 3:
            return False

        # Check for group (same number, different colors)
        numbers = set()
        colors = set()
        for tile in meld:
            if not tile.is_joker:
                numbers.add(tile.number)
                colors.add(tile.color)

        if len(numbers) == 1 and len(meld) == len(colors) + meld.count(JOKER):
            return True

        # Check for run (same color, consecutive numbers allowing jokers in the middle)
        non_jokers = [tile for tile in meld if not tile.is_joker]
        jokers_count = len(meld) - len(non_jokers)

        if not non_jokers:
            return False

        sorted_non_jokers = sorted(non_jokers, key=lambda t: t.number)
        color = sorted_non_jokers[0].color
        if any(tile.color != color for tile in non_jokers):
            return False

        # Count gaps between sorted numbers
        expected = sorted_non_jokers[0].number
        gaps = 0
        for tile in sorted_non_jokers:
            if tile.number != expected:
                gaps += tile.number - expected
            expected = tile.number + 1

        if gaps <= jokers_count:
            return True

        return False


