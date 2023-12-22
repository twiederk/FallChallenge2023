import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VisibleCreaturesTest {

    @Test
    fun should_return_empty_list_when_no_there_are_no_visible_creatures() {

        // act
        val monsters = VisibleCreatures().monsters(Creatures())

        // assert
        assertThat(monsters).isEmpty()
    }

    @Test
    fun should_return_empty_list_when_no_monsters_are_in_visible_creatures() {

        // arrange
        val visibleCreatures = VisibleCreatures().apply {
            add(VisibleCreature(creatureId = 0))
            add(VisibleCreature(creatureId = 1))
            add(VisibleCreature(creatureId = 2))
        }

        // act
        val monsters = visibleCreatures.monsters(Creatures())

        // assert
        assertThat(monsters).isEmpty()
    }

    @Test
    fun should_return_list_of_monsters_when_monsters_are_in_visible_creatures() {

        // arrange
        val monster = Creature(1, type = -1, color = -1)
        val creatures = Creatures(
            creatures = mapOf(monster.creatureId to monster)
        )
        val visibleCreatures = VisibleCreatures().apply {
            add(VisibleCreature(creatureId = 0))
            add(VisibleCreature(creatureId = 1))
            add(VisibleCreature(creatureId = 2))
        }

        // act
        val monsters = visibleCreatures.monsters(creatures)

        // assert
        assertThat(monsters).containsExactly(VisibleCreature(creatureId = 1))
    }

}