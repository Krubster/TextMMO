package ru.alastar.main.executors;

import ru.alastar.game.Skill;
import ru.alastar.main.net.Client;

public class SkillsCommandExecutor extends CommandExecutor {
	public SkillsCommandExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Shows all of your current skills Usage: client skills";
	}
  @Override
  public void execute(String[] args)
  {
	  if(numOfArgs == args.length){
		  Skill skill;
			System.out.println("---* SKILLS *---");
			System.out.println(" >--<");

		    for(String s: Client.controlledEntity.skills.keySet())
		    {
		    	skill = Client.controlledEntity.skills.get(s);
		    	System.out.println(" " +s + " - " + skill.value + "/" + skill.maxValue);
				System.out.println(" >--<");

		    }
	  }
	  else
	  {
		  System.out.println(this.description);
	  }
  }
}
