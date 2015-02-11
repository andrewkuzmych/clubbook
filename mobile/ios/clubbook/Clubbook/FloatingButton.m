//
//  FloatingButton.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "FloatingButton.h"

@implementation FloatingButton

+ (id)buttonWithFrame:(CGRect)frame {
    return [[self alloc] initWithFrame:frame];
}

- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self initButton];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self initButton];
    }
    return self;
}

- (void) initButton {
    self.layer.masksToBounds = NO;
    self.titleLabel.textAlignment = NSTextAlignmentCenter;
    self.layer.cornerRadius = 40;
    self.layer.shadowColor = [UIColor colorWithWhite:0.000 alpha:0.710].CGColor;
    self.layer.shadowOpacity = 0.5;
    self.titleEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 0);
    self.layer.shadowRadius = 7;
    self.layer.shadowOffset = CGSizeMake(7.0f, 7.0f);
}

- (void) setMainState:(NSString*) text
{
    normalColor = [UIColor colorWithRed:63/255.0 green:210/255.0 blue:18/255.0 alpha:1.0];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:41/255.0 green:13/255.0 blue:52/255.0 alpha:1.0];
    
    [self setTitle:text forState:UIControlStateNormal];
    self.statusOn = NO;
}

- (void) setSecondState:(NSString*) text
{
    [self setTitle:text forState:UIControlStateNormal];
    normalColor = [UIColor colorWithRed:161/255.0 green:23/255.0 blue:24/255.0 alpha:1.0];
    self.backgroundColor = normalColor;
    highlightedColor = [UIColor colorWithRed:245/255.0 green:35/255.0 blue:36/255.0 alpha:1.0];
    self.statusOn = YES;
}

- (UIColor*) backgroundColorForState:(UIControlState) _state {
    return [backgroundStates objectForKey:[NSNumber numberWithInt:_state]];
}


- (void) setHighlighted:(BOOL)highlighted {
    [super setHighlighted:highlighted];
    
    if (highlighted) {
        self.backgroundColor = [UIColor colorWithRed:0.698 green:0.000 blue:0.000 alpha:1.000];
    }
    else {
        self.backgroundColor = [UIColor colorWithRed:0.000 green:0.643 blue:0.000 alpha:1.000];
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
