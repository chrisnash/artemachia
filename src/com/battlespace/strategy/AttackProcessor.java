package com.battlespace.strategy;

import com.battlespace.domain.Deployment;
import com.battlespace.service.Roller;

// processes the attacks that were outlined in an attack plan. The idea is that some strategies
// (see halifix's research) process the attacking ships in a particular order, and if an earlier
// ship kills a defender, later ships won't target that same ship any more.
public interface AttackProcessor
{
    public void process(AttackPlan plan, Deployment defenders, Roller rng, double multiplier) throws Exception;
}
