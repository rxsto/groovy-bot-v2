package io.groovybot.bot.core.audio.playlists;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.utils.Checks;

import java.util.*;

/**
 * https://github.com/Stupremee
 *
 * @author: Stu
 */
public abstract class BasicQueue extends LinkedList<AudioTrack> {

    public void move(int index, int position) {
        if (index >= 0 && index < size())
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds.");

        AudioTrack track = get(index);
        remove(index);
        add(position, track);
    }

    public void remove(Object... objects) {
        Checks.notEmpty(objects, "Object array");

        for (Object o : objects) {
            remove(o);
        }
    }

    public void add(AudioTrack... tracks) {
        Checks.notEmpty(tracks, "Tracks ");

        this.addAll(Arrays.asList(tracks));
    }
}
//public abstract class BasicQueue implements Queue<AudioTrack> {
//
//    private final LinkedList<AudioTrack> storage;
//
//    protected BasicQueue() {
//        this.storage = new LinkedList<>();
//    }
//
//    @Override
//    public int size() {
//        return storage.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return storage.isEmpty();
//    }
//
//    @Override
//    public boolean contains(Object o) {
//        return storage.contains(o);
//    }
//
//    @NotNull
//    @Override
//    public Iterator<AudioTrack> iterator() {
//        return storage.iterator();
//    }
//
//    @NotNull
//    @Override
//    public Object[] toArray() {
//        return storage.toArray();
//    }
//
//    @SuppressWarnings("SuspiciousToArrayCall")
//    @NotNull
//    @Override
//    public <T> T[] toArray(@NotNull T[] a) {
//        return storage.toArray(a);
//    }
//
//    @Override
//    public <T> T[] toArray(IntFunction<T[]> generator) {
//        return storage.toArray(generator);
//    }
//
//    @Override
//    public boolean add(AudioTrack audioTrack) {
//        return storage.add(audioTrack);
//    }
//
//    @Override
//    public boolean remove(Object o) {
//        return storage.remove(o);
//    }
//
//    @Override
//    public boolean containsAll(@NotNull Collection<?> c) {
//        return storage.containsAll(c);
//    }
//
//    @Override
//    public boolean addAll(@NotNull Collection<? extends AudioTrack> c) {
//        return storage.addAll(c);
//    }
//
//    @Override
//    public boolean removeAll(@NotNull Collection<?> c) {
//        return storage.removeAll(c);
//    }
//
//    @Override
//    public boolean removeIf(Predicate<? super AudioTrack> filter) {
//        return storage.removeIf(filter);
//    }
//
//    @Override
//    public boolean retainAll(@NotNull Collection<?> c) {
//        return storage.retainAll(c);
//    }
//
//    @Override
//    public void clear() {
//        storage.clear();
//    }
//
//    @Override
//    public Spliterator<AudioTrack> spliterator() {
//        return storage.spliterator();
//    }
//
//    @Override
//    public Stream<AudioTrack> stream() {
//        return storage.stream();
//    }
//
//    @Override
//    public Stream<AudioTrack> parallelStream() {
//        return storage.parallelStream();
//    }
//
//    @Override
//    public boolean offer(AudioTrack audioTrack) {
//        return storage.offer(audioTrack);
//    }
//
//    @Override
//    public AudioTrack remove() {
//        return storage.remove();
//    }
//
//    @Override
//    public AudioTrack poll() {
//        return storage.poll();
//    }
//
//    @Override
//    public AudioTrack element() {
//        return storage.element();
//    }
//
//    @Override
//    public AudioTrack peek() {
//        return storage.peek();
//    }
//}
