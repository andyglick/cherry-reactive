package io.magentys.cherry.reactive.common;

import io.magentys.cherry.reactive.models.Failure;
import scala.concurrent.Future;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;


@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unused"})
public final class Either<L, R>
{
  private final Optional<L> left;
  private final Optional<R> right;

  private Either(Optional<L> l, Optional<R> r)
  {
    left = l;
    right = r;
  }

  public static <L, R> Either<L, R> left(L value)
  {
    return new Either<>(Optional.of(value), Optional.empty());
  }

  public static <L, R> Either<L, R> right(R value)
  {
    return new Either<>(Optional.empty(), Optional.of(value));
  }

  public <T> T map(Function<? super L, ? extends T> lFunc, Function<? super R, ? extends T> rFunc)
  {
    return left.map(lFunc).orElseGet(() -> right.map(rFunc).get());
  }

  public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> lFunc)
  {
    return new Either<>(left.map(lFunc), right);
  }

  public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rFunc)
  {
    return new Either<>(left, right.map(rFunc));
  }

  public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc)
  {
    left.ifPresent(lFunc);
    right.ifPresent(rFunc);
  }

  public static <F> Either<Future<F>, Failure> empty()
  {
    return new Either<>(Optional.empty(), Optional.empty());
  }

  public Optional<L> left()
  {
    return left;
  }

  public Optional<R> right()
  {
    return right;
  }
}
