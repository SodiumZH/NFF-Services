package net.sodiumzh.nfu.entity.vanillatrade;

import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.sodiumzh.nfu.container.Tuple2;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtendableVanillaTradeOfferList<T> {

    private MerchantOffers offerList;
    private List<T> extensionList;

    public ExtendableVanillaTradeOfferList() {
        this.offerList = new MerchantOffers();
        this.extensionList = new ArrayList<>();
    }

    public ExtendableVanillaTradeOfferList<T> init(List<MerchantOffer> offers, List<T> extensions) {
        this.offerList.clear();
        this.extensionList.clear();
        for (int i = 0; i < offers.size(); ++i) {
            this.offerList.add(offers.get(i));
            if (i < extensions.size()) this.extensionList.add(extensions.get(i));
            else extensionList.add(null);
        }
        return this;
    }

    public ExtendableVanillaTradeOfferList<T> init(List<MerchantOffer> offers,
                                                   Function<MerchantOffer, T> extensionInitializer) {
        return this.init(offers, offers.stream().map(extensionInitializer).collect(Collectors.toList()));
    }

    public ExtendableVanillaTradeOfferList<T> init(Map<MerchantOffer, T> offersAndExtensions) {
        this.offerList.clear();
        this.extensionList.clear();
        offersAndExtensions.forEach((key, value) -> {
            this.offerList.add(key);
            this.extensionList.add(value);
        });
        return this;
    }

    public ExtendableVanillaTradeOfferList<T> init(List<Tuple2<MerchantOffer, T>> offersAndExtensions) {
        this.offerList.clear();
        this.extensionList.clear();
        offersAndExtensions.forEach(entry -> {
            this.offerList.add(entry.getA());
            this.extensionList.add(entry.getB());
        });
        return this;
    }

    public Tuple2<MerchantOffer, T> get(int index) {
        return Tuple2.of(offerList.get(index), extensionList.get(index));
    }

    public void add(MerchantOffer offer, T extension) {
        this.offerList.add(offer);
        this.extensionList.add(extension);
    }

    public void set(int position, MerchantOffer offer, T extension) {
        this.offerList.set(position, offer);
        this.extensionList.set(position, extension);
    }

    public void clear() {
        this.offerList.clear();
        this.extensionList.clear();
    }

    @Nullable
    public T getExtension(MerchantOffer offer) {
        int i = this.offerList.indexOf(offer);
        if (i < 0) return null;
        return extensionList.get(i);
    }

    @Nullable
    public MerchantOffer getOffer(T extension) {
        int i = this.extensionList.indexOf(extension);
        if (i < 0) return null;
        return offerList.get(i);
    }

    public int indexOfOffer(MerchantOffer offer) {
        return this.offerList.indexOf(offer);
    }

    public int indexOfExtension(T extension) {
        return this.extensionList.indexOf(extension);
    }

    public List<Tuple2<MerchantOffer, T>> toPairList() {
        List<Tuple2<MerchantOffer, T>> res = new ArrayList<>();
        for (int i = 0; i < this.offerList.size(); ++i) {
            res.add(this.get(i));
        }
        return res;
    }

    public void sort(Comparator<MerchantOffer> comparator) {
        List<Tuple2<MerchantOffer, T>> list = this.toPairList();
        this.clear();
        list.stream().sorted(Comparator.comparing(Tuple2::getA, comparator)).forEach(entry -> this.add(entry.getA(), entry.getB()));
    }

    public MerchantOffers toOffers() {
        return this.offerList;
    }

    public List<MerchantOffer> getOffersCopy() {
        return List.copyOf(this.offerList);
    }

}
