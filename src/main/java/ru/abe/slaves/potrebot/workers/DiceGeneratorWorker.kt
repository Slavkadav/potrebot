package ru.abe.slaves.potrebot.workers

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage
import kotlin.math.abs

@Order(1)
@Component
class DiceGeneratorWorker(private val vkService: VkService) : Worker {
    override suspend fun regex(): Regex = Regex("${regexPrefix}кинь.([0-9]{1,3})[dкд]([0-9]{1,3})", RegexOption.IGNORE_CASE)

    override suspend fun reactToMessage(vkMessage: VkMessage) {
        reactToThrowDiceXdY(vkMessage)
    }

    private suspend fun reactToThrowDiceXdY(vkMessage: VkMessage) {
        val answer = throwDice(vkMessage.text)
        vkService.sendMessage(vkMessage.chatId, answer)
    }

    private suspend fun throwDice(text: String): String {
        // Накидать рандомных чисел в пределе указанном в сообщении
        val diceGroups = regex().find(text)?.groupValues
        val numberOfDice = diceGroups?.get(1)?.toInt() ?: 0
        val diceSize = diceGroups?.get(2)?.toInt() ?: 0

        if (numberOfDice == 0) {
            return "Не получится сломать, брат"
        }

        val diceResults = mutableMapOf<Int, Int>()
        repeat(numberOfDice) {
            val diceValue = (1..diceSize).random()
            diceResults.computeIfPresent(diceValue) { _, value ->
                value + 1
            }
            diceResults.putIfAbsent(diceValue, 1)
        }
        // Вывод результата
        val diceResultSum = diceResults.map { it.key * it.value }.sum()

        val diceResultList = diceResults.entries.flatMap { generateSequence { it.key }.take(it.value) }

        val result = "$diceResultList = $diceResultSum"

        // Особые результаты
        if (diceSize == 6) {
            val middleValue = numberOfDice / 2
            // 5+ больше половины
            if (diceResults.filter { it.key >= 5 }.map { it.value }.sum() >= middleValue) {
                return "$result\nЧел, хорош!"
            }
            // 2- больше половины
            if (diceResults.filter { it.key <= 2 }.map { it.value }.sum() >= middleValue) {
                return "$result\nЛооох!"
            }
            // среднее = размерность куба + 1 пополам
            if (abs(middleValue - diceResultSum / numberOfDice) < 0.5) {
                return "$result\nТупа по стате!"
            }
        }
        return result
    }
}