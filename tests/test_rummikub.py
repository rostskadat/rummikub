import pytest
from rummikub import RummikubController, Tile, TileColor
from pprint import pprint

@pytest.fixture
def game():
    # 2 players, 1 human, deterministic seed for reproducibility
    return RummikubController(None, 2, 1, seed=42)

#------------------------------------------------------------------------------
#
# FINDING RUNS AND GROUPS
#
#------------------------------------------------------------------------------
def _t(number:int, color:str):
    return Tile(False, number, TileColor[color.upper()])

def test_find_sets_0_run(game):
    # No valid sets
    tiles = [_t(1, 'red'), _t(2, 'blue'), _t(3, 'orange'), _t(4, 'black'), _t(5, 'black')]
    sets = game._find_sets(tiles)
    assert sets == []

def test_find_sets_0_group(game):
    # No valid sets
    tiles = [_t(1, 'red'), _t(1, 'blue'), _t(1, 'red'), _t(2, 'red'), _t(2, 'blue')]
    sets = game._find_sets(tiles)
    assert sets == []

def test_find_sets_1_run(game):
    # Red 1, 2, 3 should form a run
    tiles = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    sets = game._find_sets(tiles)
    assert any(
        sorted([_t(t.number, t.color.name.lower()) for t in meld]) ==
        sorted([_t(1, 'red'), _t(2, 'red'), _t(3, 'red')])
        for meld in sets
    )

def test_find_sets_1_run_with_joker(game):
    # Red 1, 2, joker should form a run 1-2-3
    tiles = [_t(1, 'red'), _t(2, 'red'), Tile(True)]
    sets = game._find_sets(tiles)
    assert any(
        len(meld) == 3 and
        sum(t.is_joker for t in meld) == 1 and
        set(t.number for t in meld if not t.is_joker) == {1, 2}
        for meld in sets
    )

def test_find_sets_1_run_long(game):
    # Run of 1-2-3-4-5 in blue
    tiles = [_t(n, 'blue') for n in range(1, 6)]
    sets = game._find_sets(tiles)
    assert len(sets) ==  6 # 1 run of length 5, plus sub-runs of length 3 and 4
    assert any(
        len(meld) >= 5 and
        all(t.color == TileColor.BLUE for t in meld)
        for meld in sets
    )

def test_find_sets_1_group(game):
    # 3 tiles of number 5, different colors
    tiles = [_t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')]
    sets = game._find_sets(tiles)
    assert any(
        sorted([_t(t.number, t.color.name.lower()) for t in meld]) ==
        sorted([_t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')])
        for meld in sets
    )

def test_find_sets_1_group_with_joker(game):
    # 2 colors of number 7, plus joker, should form group of 3
    tiles = [_t(7, 'red'), _t(7, 'blue'), Tile(True)]
    sets = game._find_sets(tiles)
    assert any(
        len(meld) == 3 and
        sum(t.is_joker for t in meld) == 1 and
        set(t.number for t in meld if not t.is_joker) == {7}
        for meld in sets
    )

def test_find_sets_1_group_no_duplicate_colors(game):
    # Only unique colors should be used in group
    tiles = [_t(8, 'red'), _t(8, 'red'), _t(8, 'blue'), _t(8, 'orange')]
    sets = game._find_sets(tiles)
    # Should not use both reds
    for meld in sets:
        if all(t.number == 8 for t in meld):
            colors = [t.color for t in meld if not t.is_joker]
            assert len(colors) == len(set(colors))

def test_find_sets_1_run_1_group(game):
    tiles = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red'), _t(3, 'blue'), _t(3, 'orange')]
    sets = game._find_sets(tiles)
    assert len(sets) == 2
    (run, group) = (sets[0], sets[1])
    assert sorted([_t(t.number, t.color.name.lower()) for t in run]) == sorted([_t(1, 'red'), _t(2, 'red'), _t(3, 'red')])
    assert sorted([_t(t.number, t.color.name.lower()) for t in group]) == sorted([_t(3, 'red'), _t(3, 'blue'), _t(3, 'orange')])

#------------------------------------------------------------------------------
#
# COUNTING VALUE
#
#------------------------------------------------------------------------------
def test_sum_melds_1_run(game):
    tiles = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    value = game._sum_melds([tiles])
    assert value == 6

def test_sum_melds_1_group(game):
    tiles = [_t(1, 'red'), _t(1, 'blue'), _t(1, 'orange')]
    value = game._sum_melds([tiles])
    assert value == 3

def test_sum_melds_1_joker(game):
    tiles = [_t(1, 'red'), _t(1, 'blue'), Tile(True)]
    value = game._sum_melds([tiles])
    assert value == 2

#------------------------------------------------------------------------------
#
# SELECTING VALID MELD
#
#------------------------------------------------------------------------------
def test_validate_meld_selection_valid(game):
    player = game.players[0]
    player.has_played_initial = True
    # Give player tiles for two valid melds
    player.tiles = [
        _t(1, 'red'), _t(2, 'red'), _t(3, 'red'),
        _t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')
    ]
    sets = [
        [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')],
        [_t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')]
    ]
    # Should accept both melds if selected
    result = game._validate_meld_selection(player, sets, [0, 1])
    pprint(f"result={result}")
    pprint(f"set={set}")
    assert result == sets

def test_validate_meld_selection_overlapping_tile(game):
    player = game.players[0]
    # Only one Tile(5, 'red'), but both melds use it
    player.tiles = [
        _t(1, 'red'), _t(2, 'red'), _t(3, 'red'),
        _t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')
    ]
    sets = [
        [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')],
        [_t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')],
        [_t(5, 'red'), _t(5, 'blue'), _t(5, 'orange')]  # duplicate meld for overlap
    ]
    # Select two melds that both use Tile(5, 'red') (overlap)
    result = game._validate_meld_selection(player, sets, [1, 2])
    assert result == []

def test_validate_meld_selection_not_enough_points_initial(game):
    player = game.players[0]
    player.has_played_initial = False
    player.tiles = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    sets = [[_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]]  # sum = 6 < 30
    result = game._validate_meld_selection(player, sets, [0])
    assert result == []

def test_validate_meld_selection_enough_points_initial(game):
    player = game.players[0]
    player.has_played_initial = False
    player.tiles = [_t(10, 'red'), _t(11, 'red'), _t(12, 'red')]
    sets = [[_t(10, 'red'), _t(11, 'red'), _t(12, 'red')]]  # sum = 33 >= 30
    result = game._validate_meld_selection(player, sets, [0])
    assert result == sets

def test_validate_meld_selection_after_initial(game):
    player = game.players[0]
    player.has_played_initial = True
    player.tiles = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    sets = [[_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]]
    result = game._validate_meld_selection(player, sets, [0])
    assert result == sets

def test_validate_meld_selection_invalid_index(game):
    player = game.players[0]
    player.has_played_initial = True
    player.tiles = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    sets = [[_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]]
    # Index 1 is out of range, should ignore and return empty
    result = game._validate_meld_selection(player, sets, [1])
    assert result == []

def test_validate_meld_selection_duplicate_tile_usage(game):
    player = game.players[0]
    player.has_played_initial = True
    # Only one Tile(1, 'red'), but two melds both use it
    player.tiles = [
        _t(1, 'red'), _t(2, 'red'), _t(3, 'red'),
        _t(3, 'blue'), _t(3, 'orange')
    ]
    sets = [
        [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')],
        [_t(3, 'red'), _t(3, 'blue'), _t(3, 'orange')]
    ]
    # Selecting both melds would require two Tile(1, 'red')
    result = game._validate_meld_selection(player, sets, [0, 1])
    assert result == []

#------------------------------------------------------------------------------
#
# TESTS FOR _select_best_melds
#
#------------------------------------------------------------------------------
@pytest.mark.parametrize("strategy,key_func", [
    ('highest-value', lambda game, meld: game._sum_melds([meld])),
    ('longest-run', lambda game, meld: len(meld)),
    ('all-in', lambda game, meld: None),  # all-in just returns all non-overlapping melds
])
def test_select_best_melds_non_overlapping(game, strategy, key_func):
    # Setup melds: two non-overlapping, one overlapping
    meld1 = [_t(1, 'red'),  _t(2, 'red'),  _t(3, 'red')]    # sum=6, len=3
    meld2 = [_t(4, 'blue'), _t(5, 'blue'), _t(6, 'blue')]   # sum=15, len=3
    meld3 = [_t(1, 'red'),  _t(5, 'blue'), _t(7, 'orange')] # overlaps with meld1 and meld2
    melds = [meld1, meld2, meld3]

    game.strategy = strategy
    result = game._select_best_melds(melds)

    # Should only select meld1 and meld2 (no overlap)
    assert meld1 in result
    assert meld2 in result
    assert meld3 not in result
    # Should not use any tile more than once
    used_tiles = [tile for meld in result for tile in meld]
    assert len(used_tiles) == len(set(used_tiles))

def test_select_best_melds_highest_value(game):
    # Setup melds with different values
    meld1 = [_t(1,  'red'),    _t(2,  'red'),    _t(3,  'red')]  # sum=6
    meld2 = [_t(10, 'blue'),   _t(11, 'blue'),   _t(12, 'blue')]  # sum=33
    meld3 = [_t(4,  'orange'), _t(5,  'orange'), _t(6,  'orange')]  # sum=15
    melds = [meld1, meld2, meld3]

    game.strategy = 'highest-value'
    result = game._select_best_melds(melds)
    # Highest value meld should be first
    assert result[0] == meld2
    # No overlapping tiles
    used_tiles = [tile for meld in result for tile in meld]
    assert len(used_tiles) == len(set(used_tiles))

def test_select_best_melds_longest_run(game):
    # Setup melds with different lengths
    meld1 = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]  # len=3
    meld2 = [_t(4, 'blue'), _t(5, 'blue'), _t(6, 'blue'), _t(7, 'blue')]  # len=4
    meld3 = [_t(8, 'orange'), _t(9, 'orange')]  # len=2 (should not be selected)
    melds = [meld1, meld2, meld3]

    game.strategy = 'longest-run'
    result = game._select_best_melds(melds)
    # Longest run meld should be first
    assert result[0] == meld2
    # No overlapping tiles
    used_tiles = [tile for meld in result for tile in meld]
    assert len(used_tiles) == len(set(used_tiles))

def test_select_best_melds_all_in(game):
    # Should select as many non-overlapping melds as possible
    meld1 = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    meld2 = [_t(4, 'blue'), _t(5, 'blue'), _t(6, 'blue')]
    meld3 = [_t(7, 'orange'), _t(8, 'orange'), _t(9, 'orange')]
    melds = [meld1, meld2, meld3]

    game.strategy = 'all-in'
    result = game._select_best_melds(melds)
    # Should select all melds (no overlap)
    assert set(tuple(tuple((t.number, t.color, t.is_joker) for t in meld) for meld in result)) == \
            set(tuple(tuple((t.number, t.color, t.is_joker) for t in meld) for meld in melds))

def test_select_best_melds_overlap_prevents_selection(game):
    # Overlapping melds: only one can be selected
    meld1 = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    meld2 = [_t(3, 'red'), _t(4, 'red'), _t(5, 'red')]  # overlaps on Tile(3, 'red')
    melds = [meld1, meld2]

    game.strategy = 'highest-value'
    result = game._select_best_melds(melds)
    # Only one meld should be selected
    assert len(result) == 1
    assert result[0] in melds

def test_select_best_melds_empty_input(game):
    game.strategy = 'highest-value'
    result = game._select_best_melds([])
    assert result == []

def test_select_best_melds_strategy_not_recognized(game):
    # Should just return melds as-is if strategy is not recognized
    meld1 = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    meld2 = [_t(4, 'blue'), _t(5, 'blue'), _t(6, 'blue')]
    melds = [meld1, meld2]
    game.strategy = 'unknown-strategy'
    result = game._select_best_melds(melds)
    assert result == melds


#------------------------------------------------------------------------------
#
# TESTS FOR _sort_table
#
#------------------------------------------------------------------------------
def test_sort_table_by_value_then_length_then_lowest_tile(game):
    # Create melds with different values, lengths, and lowest tiles
    meld1 = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]  # value=6, len=3, lowest=1
    meld2 = [_t(2, 'blue'), _t(3, 'blue'), _t(4, 'blue')]  # value=9, len=3, lowest=2
    meld3 = [_t(1, 'orange'), _t(2, 'orange')]  # value=3, len=2, lowest=1
    meld4 = [_t(1, 'black'), _t(2, 'black'), _t(3, 'black'), _t(4, 'black')]  # value=10, len=4, lowest=1
    meld5 = [_t(1, 'red'), _t(1, 'blue'), _t(1, 'orange')]  # value=3, len=3, lowest=1

    game.table = [meld1, meld2, meld3, meld4, meld5]
    game._sort_table()

    # Should be sorted by value, then length, then lowest tile
    # meld3 and meld5 have value=3, meld3 is shorter (len=2), so comes first
    # meld1: value=6, meld2: value=9, meld4: value=10
    assert game.table == [meld3, meld5, meld1, meld2, meld4]

def test_sort_table_with_jokers(game):
    # Jokers should not count towards meld_value or lowest tile
    meld1 = [_t(1, 'red'), _t(2, 'red'), Tile(True)]  # value=3, len=3, lowest=1
    meld2 = [_t(2, 'blue'), _t(3, 'blue'), Tile(True)]  # value=5, len=3, lowest=2
    meld3 = [_t(1, 'orange'), Tile(True)]  # value=1, len=2, lowest=1

    game.table = [meld1, meld2, meld3]
    game._sort_table()

    # meld3: value=1, len=2, lowest=1
    # meld1: value=3, len=3, lowest=1
    # meld2: value=5, len=3, lowest=2
    assert game.table == [meld3, meld1, meld2]

def test_sort_table_equal_value_and_length(game):
    # If value and length are equal, sort by lowest tile number
    meld1 = [_t(2, 'red'), _t(3, 'red'), _t(4, 'red')]  # value=9, len=3, lowest=2
    meld2 = [_t(1, 'blue'), _t(4, 'blue'), _t(4, 'orange')]  # value=9, len=3, lowest=1

    game.table = [meld1, meld2]
    game._sort_table()

    # meld2 has lower lowest tile (1 vs 2)
    assert game.table == [meld2, meld1]

def test_sort_table_empty(game):
    game.table = []
    game._sort_table()
    assert game.table == []

def test_sort_table_all_equal(game):
    meld = [_t(1, 'red'), _t(2, 'red'), _t(3, 'red')]
    game.table = [meld.copy(), meld.copy(), meld.copy()]
    game._sort_table()
    # Order should be preserved (stable sort)
    assert game.table == [meld, meld, meld]



