package com.xereon.xereon.util.lists.diffutil

interface HasPayloadDiffer {
    fun diffPayload(old: Any, new: Any): Any?
}