//
//  DateHelper.m
//  Clubbook
//
//  Created by Anton Semenyuk on 12/22/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "DateHelper.h"

@implementation DateHelper

+ (DateHelper*)sharedSingleton {
    static DateHelper* sharedSingleton;
    if(!sharedSingleton) {
        @synchronized(sharedSingleton) {
            sharedSingleton = [DateHelper new];
        }
    }
    
    return sharedSingleton;
}

- (BOOL) is24hFormat {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setLocale:[NSLocale currentLocale]];
    [formatter setDateStyle:NSDateFormatterNoStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    NSString *dateString = [formatter stringFromDate:[NSDate date]];
    NSRange amRange = [dateString rangeOfString:[formatter AMSymbol]];
    NSRange pmRange = [dateString rangeOfString:[formatter PMSymbol]];
    BOOL is24h = (amRange.location == NSNotFound && pmRange.location == NSNotFound);

    return is24h;
}

- (NSString*) get24hTime:(NSDate*) date {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateFormat = @"HH:mm";
    NSString *dateString = [dateFormatter stringFromDate:date];
    return dateString;
}

- (NSString*) get12hTime:(NSDate*) date {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
   
    dateFormatter.dateFormat = @"h:mm a";
    NSString *pmamDateString = [dateFormatter stringFromDate:date];
    return pmamDateString;
}

@end
