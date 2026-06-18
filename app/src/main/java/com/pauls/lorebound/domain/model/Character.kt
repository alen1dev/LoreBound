package com.pauls.lorebound.domain.model

data class Character(
    val id: Long = 0L,
    val name: String,
    val strength: Int,
    val intelligence: Int,
    val charisma: Int,
    val creativity: Int,
    val exploration: Int,
    val courage: Int,
    val totalXp: Long = 0L,
    val currentTitle: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String? = null
) {
    fun traitValue(trait: Trait): Int = when (trait) {
        Trait.STRENGTH -> strength
        Trait.INTELLIGENCE -> intelligence
        Trait.CHARISMA -> charisma
        Trait.CREATIVITY -> creativity
        Trait.EXPLORATION -> exploration
        Trait.COURAGE -> courage
    }

    fun weakTraits(): List<Trait> = Trait.entries.sortedBy { traitValue(it) }.take(2)

    fun strongTraits(): List<Trait> = Trait.entries.sortedByDescending { traitValue(it) }.take(2)

    fun withTraitIncrease(trait: Trait, amount: Int): Character = when (trait) {
        Trait.STRENGTH -> copy(strength = strength + amount)
        Trait.INTELLIGENCE -> copy(intelligence = intelligence + amount)
        Trait.CHARISMA -> copy(charisma = charisma + amount)
        Trait.CREATIVITY -> copy(creativity = creativity + amount)
        Trait.EXPLORATION -> copy(exploration = exploration + amount)
        Trait.COURAGE -> copy(courage = courage + amount)
    }
}
