package com.homeworker.domain

import java.time.LocalDateTime

trait Model:
  def id: Option[Long]
  def createdAt: LocalDateTime
  def updatedAt: LocalDateTime 