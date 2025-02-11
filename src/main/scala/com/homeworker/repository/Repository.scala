package com.homeworker.repository

import cats.effect.IO
import doobie.util.query.Query0
import doobie.util.update.Update0
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.transactor.Transactor

trait Repository[F[_], A]:
  def xa: Transactor[F]

  protected def query[B](q: Query0[B]): F[List[B]]
  protected def update(u: Update0): F[Int] 