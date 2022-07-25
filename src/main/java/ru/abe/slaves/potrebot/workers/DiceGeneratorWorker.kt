package ru.abe.slaves.potrebot.workers

import org.springframework.stereotype.Component
import ru.abe.slaves.potrebot.VkService
import ru.abe.slaves.potrebot.web.model.VkMessage

@Component
class DiceGeneratorWorker(private val vkService: VkService): Worker {
    override fun regex(): Regex = Regex("${regexPrefix}[Кк]убас.[1234567890]{1,3}[DdКкДд][1234567890]{1,3}", RegexOption.IGNORE_CASE)

    override fun reactToMessage(vkMessage: VkMessage) {
        reactToThrowDiceXdY(vkMessage)
    }

    private fun reactToThrowDiceXdY(vkMessage: VkMessage){
        val answer = ThrowDice(vkMessage.text)
        vkService.sendMessage(vkMessage.chatId, answer )
    }

    private fun ThrowDice(text: String): String {
        var sum=0;
        val d=arrayOf<Int>(0,0)
        val countValueDice=arrayOf<Int>(0,0)
        var i:Int=0
        val patternForText ="[Кк]убас.[1234567890]{1,3}[DdКкДд][1234567890]{1,3}".toRegex()
        val matchForText = patternForText.find(text)
        val XdY=matchForText?.value.toString()
        val patternForDice="\\d{1,3}".toRegex()
        val matchForDices = patternForDice.findAll(XdY)
        matchForDices.forEach{ f ->
            d[i]=f.value.toInt()
            i+=1
        }
        if(d[0]==0){
            return "Не получится сломать, брат"
        }
        var res="("
        for(i in 1..d[0]){
            var dice = (1..d[1]).random()
            if(d[1]==6){
                when(dice){
                    1,2->countValueDice[0]+=1
                    5,6->countValueDice[1]+=1
                }
            }
            if(i==d[0]){
                res+="$dice"
            }
            else{
                res+="$dice, "
            }
            sum+=dice
        }
        res+= ") = $sum\n"
        if(d[1]==6){
            if(countValueDice[0]>=d[0]/2){
                res+=" Лооох! "
            }
            if(countValueDice[1]>=d[0]/2){
                res+=" Чел, хорош! "
            }
            if(countValueDice[0]==countValueDice[1]){
                res+=" Тупа по стате "
            }
        }
        return res
    }
}