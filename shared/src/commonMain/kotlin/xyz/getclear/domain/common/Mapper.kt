package xyz.getclear.domain.common

interface  Mapper<FROM, TO> {
    operator fun invoke(from: FROM) : TO
}