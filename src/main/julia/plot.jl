using JSON, DataFrames, CSV
using Cairo, Fontconfig, Gadfly
using StatsBase

durations = CSV.read("data/V3C_durations.csv", DataFrame)


function parse_tsv_line(line)
    s = split(line, "\t")
    d = JSON.parse(s[2])

    #sanity check and data cleaning
    for k in keys(d)
        d[k] = max(0, d[k])
    end
    x = sum(values(d))
    for k in keys(d)
        d[k] = d[k] / x
    end

    return s[1] => d
end

v3c1 = Dict(map(x -> parse_tsv_line(x), readlines("data/V3C1_proportional.tsv")))
v3c2 = Dict(map(x -> parse_tsv_line(x), readlines("data/V3C2_proportional.tsv")))
v3c3 = Dict(map(x -> parse_tsv_line(x), readlines("data/V3C3_proportional.tsv")))

### fraction of video covered by (any) language

v3c1_hist = countmap(map(x -> round(Int, (1 - x["NONE"]) * 50) / 50, values(v3c1)))
v3c2_hist = countmap(map(x -> round(Int, (1 - x["NONE"]) * 50) / 50, values(v3c2)))
v3c3_hist = countmap(map(x -> round(Int, (1 - x["NONE"]) * 50) / 50, values(v3c3)))

s1 = sum(values(v3c1_hist))
s2 = sum(values(v3c2_hist))
s3 = sum(values(v3c3_hist))

k = collect(0:0.02:1)
hist = vcat(
    DataFrame(collection = "V3C1", coverage = k, videos = map(x -> get(v3c1_hist, x, 0) / s1, k)),
    DataFrame(collection = "V3C2", coverage = k, videos = map(x -> get(v3c2_hist, x, 0) / s2, k)),
    DataFrame(collection = "V3C3", coverage = k, videos = map(x -> get(v3c3_hist, x, 0) / s3, k)),
)

p = plot(
    hist, x = :coverage, y = :videos, color = :collection, Geom.line,
    Guide.XLabel("Fraction of time covered by Language"),
    Guide.YLabel("Fraction of Videos"),
    Guide.ColorKey("Collection"),
    Guide.XTicks(ticks = collect(0:0.1:1)),
    Guide.YTicks(ticks = collect(0:0.02:.16)),
)

draw(PDF("plots/language_fractions.pdf", 12cm, 8cm), p)



### fraction of video covered by music

v3c1_hist = countmap(map(x -> round(Int, get(x, "MUSIC", 0) * 50) / 50, values(v3c1)))
v3c2_hist = countmap(map(x -> round(Int, get(x, "MUSIC", 0) * 50) / 50, values(v3c2)))
v3c3_hist = countmap(map(x -> round(Int, get(x, "MUSIC", 0) * 50) / 50, values(v3c3)))

s1 = sum(values(v3c1_hist))
s2 = sum(values(v3c2_hist))
s3 = sum(values(v3c3_hist))

k = collect(0:0.02:1)
hist = vcat(
    DataFrame(collection = "V3C1", coverage = k, videos = map(x -> get(v3c1_hist, x, 0) / s1, k)),
    DataFrame(collection = "V3C2", coverage = k, videos = map(x -> get(v3c2_hist, x, 0) / s2, k)),
    DataFrame(collection = "V3C3", coverage = k, videos = map(x -> get(v3c3_hist, x, 0) / s3, k)),
)

p = plot(
    hist[hist[:, :coverage] .> 0, :], x = :coverage, y = :videos, color = :collection, Geom.line,
    Guide.XLabel("Fraction of time covered by Music"),
    Guide.YLabel("Fraction of Videos"),
    Guide.ColorKey("Collection"),
    Guide.XTicks(ticks = collect(0.1:0.1:1)),
    Guide.YTicks(ticks = collect(0:0.002:.012))
)

draw(PDF("plots/music_fractions.pdf", 12cm, 8cm), p)
