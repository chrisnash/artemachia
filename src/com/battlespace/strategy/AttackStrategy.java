package com.battlespace.strategy;

import com.battlespace.domain.Deployment;

public interface AttackStrategy
{

    AttackPlan getAttackPlan(Deployment attackDeployment,
            Deployment defendDeployment);

}
